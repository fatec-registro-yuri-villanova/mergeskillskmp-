package com.fatec.merge_skills_kmp.routes

import com.fatec.merge_skills_kmp.domain.models.Course
import com.fatec.merge_skills_kmp.domain.models.CourseInsert
import com.fatec.merge_skills_kmp.domain.models.InsertUser
import com.fatec.merge_skills_kmp.domain.models.Lesson
import com.fatec.merge_skills_kmp.domain.models.LessonInsert
import com.fatec.merge_skills_kmp.domain.models.Question
import com.fatec.merge_skills_kmp.domain.models.QuestionInsert
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

import kotlinx.serialization.Serializable

@Serializable
data class SeedResponse(val message: String, val new_courses_inserted: Int)

@Serializable
data class SeedErrorResponse(val error: String)

fun Route.adminRoutes(client: SupabaseClient) {
    route("/admin") {
        post("/seed") {
            try {
                // 1. Seed Users (Optional fallback, as Auth is preferred now, but maintaining fidelity)
                seedUser(client)

                // 2. Fetch existing courses to avoid duplicates
                val existingCourses = try {
                    client.from("courses").select().decodeList<Course>()
                } catch (e: Exception) {
                    emptyList()
                }
                val existingTitles = existingCourses.map { it.title }.toSet()

                val coursesToInsert = listOf(
                    CourseInsert("Java Icaro", "Aprenda os fundamentos da linguagem de programação Java", "code", "#E76F00"),
                    CourseInsert("Kotlin", "Domine a programação em Kotlin desde o início", "code", "#7F52FF"),
                    CourseInsert("Python", "Explore os fundamentos da programação em Python", "code", "#3776AB"),
                    CourseInsert("TypeScript", "Construa aplicações com tipagem segura usando TypeScript", "code", "#3178C6")
                ).filter { !existingTitles.contains(it.title) }

                val allCourses = existingCourses.toMutableList()

                if (coursesToInsert.isNotEmpty()) {
                    val insertedCourses = client.from("courses").insert(coursesToInsert) { select() }.decodeList<Course>()
                    allCourses.addAll(insertedCourses)
                }

                // 3. Process each course idempotently
                allCourses.forEach { course ->
                    when (course.title) {
                        "Java Icaro" -> seedCourse1(client, course.id)
                        "Kotlin" -> seedCourse2(client, course.id)
                        "Python" -> seedCourse3(client, course.id)
                        "TypeScript" -> seedCourse4(client, course.id)
                    }
                }

                call.respond(HttpStatusCode.OK, SeedResponse(message = "Database seeded successfully", new_courses_inserted = coursesToInsert.size))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, SeedErrorResponse(error = "Failed to seed database: ${e.message}"))
            }
        }
    }
}

// Workaround for PostgREST demanding all JSON array objects to have exactly matching keys.
// Supabase-kt omits null fields, causing mismatch between questions with code and without code.
private suspend fun insertQuestions(client: SupabaseClient, questions: List<QuestionInsert>) {
    val qWithCode = questions.filter { it.code != null }
    val qWithoutCode = questions.filter { it.code == null }
    
    if (qWithCode.isNotEmpty()) client.from("questions").insert(qWithCode)
    if (qWithoutCode.isNotEmpty()) client.from("questions").insert(qWithoutCode)
}

private suspend fun seedUser(client: SupabaseClient) {
    try {
        val students = client.from("users").select {
            filter { eq("username", "student") }
        }.decodeList<InsertUser>()

        if (students.isEmpty()) {
            client.from("users").insert(
                InsertUser(username = "student", email = "student@lddm.com", name = "Student User", password = "password123", role = "user", profilePicture = "https://ui-avatars.com/api/?name=Student+User&background=0D8ABC&color=fff")
            )
        }

        val admins = client.from("users").select {
            filter { eq("username", "admin") }
        }.decodeList<InsertUser>()

        if (admins.isEmpty()) {
            client.from("users").insert(
                InsertUser(username = "admin", email = "admin@lddm.com", name = "Admin User", password = "admin123", role = "admin", profilePicture = "https://ui-avatars.com/api/?name=Admin+User&background=E74C3C&color=fff")
            )
        }
    } catch (e: Exception) {
        // Silent catch for fidelity with legacy
    }
}

private suspend fun seedCourse1(client: SupabaseClient, courseId: Int) {
    val existingLessons = try { client.from("lessons").select { filter { eq("course_id", courseId) } }.decodeList<Lesson>() } catch(e: Exception) { emptyList() }
    val lessonsToInsert = listOf(
        LessonInsert(courseId, "Variáveis", "Tipos de dados, declarações e inicialização em Java", 1),
        LessonInsert(courseId, "Laços de Repetição", "Domine for, while e do-while em Java", 2),
        LessonInsert(courseId, "Funções", "Métodos, parâmetros e tipos de retorno", 3),
        LessonInsert(courseId, "Classes", "Programação orientada a objetos com classes Java", 4)
    ).filter { newLesson -> existingLessons.none { it.order == newLesson.order } }

    val allLessons = existingLessons.toMutableList()
    if (lessonsToInsert.isNotEmpty()) {
        val insertedLessons = client.from("lessons").insert(lessonsToInsert) { select() }.decodeList<Lesson>()
        allLessons.addAll(insertedLessons)
    }

    val lesson1 = allLessons.find { it.order == 1 }!!
    val existingQ1 = client.from("questions").select { filter { eq("lesson_id", lesson1.id) } }.decodeList<Question>()
    if (existingQ1.isEmpty()) {
        val questions = listOf(
            QuestionInsert(lesson1.id, "Qual é o tipo primitivo usado para armazenar números inteiros em Java?", null, listOf("float", "int", "String", "boolean"), 1, 1),
            QuestionInsert(lesson1.id, "Qual palavra reservada é usada para declarar uma constante em Java?", null, listOf("const", "static", "final", "let"), 2, 2),
            QuestionInsert(lesson1.id, "Qual é o valor padrão de uma variável int não inicializada como atributo de classe?", null, listOf("null", "0", "undefined", "-1"), 1, 3),
            QuestionInsert(lesson1.id, "Qual será o resultado deste código?", "int x = 10;\ndouble y = x;\nSystem.out.println(y);", listOf("10", "10.0", "Erro de compilação", "null"), 1, 4),
            QuestionInsert(lesson1.id, "O que este código imprime?", "String nome = \"Java\";\nint versao = 21;\nSystem.out.println(nome + \" \" + versao);", listOf("Java21", "Java 21", "Erro de compilação", "null 21"), 1, 5)
        )
        insertQuestions(client, questions)
    }

    val lesson2 = allLessons.find { it.order == 2 }!!
    val existingQ2 = client.from("questions").select { filter { eq("lesson_id", lesson2.id) } }.decodeList<Question>()
    if (existingQ2.isEmpty()) {
        val questions = listOf(
            QuestionInsert(lesson2.id, "Qual laço garante que o bloco será executado pelo menos uma vez?", null, listOf("for", "while", "do-while", "foreach"), 2, 1),
            QuestionInsert(lesson2.id, "Qual palavra-chave é usada para pular uma iteração do loop?", null, listOf("break", "skip", "continue", "pass"), 2, 2),
            QuestionInsert(lesson2.id, "Quantas vezes o loop executa?", "for (int i = 0; i < 5; i++) {\n    System.out.println(i);\n}", listOf("4", "5", "6", "Infinito"), 1, 3),
            QuestionInsert(lesson2.id, "Qual é a saída deste código?", "int i = 3;\nwhile (i > 0) {\n    System.out.print(i + \" \");\n    i--;\n}", listOf("3 2 1", "3 2 1 0", "2 1 0", "Erro"), 0, 4),
            QuestionInsert(lesson2.id, "O que acontece ao executar este código?", "for (int i = 0; i < 3; i++) {\n    if (i == 1) continue;\n    System.out.print(i + \" \");\n}", listOf("0 2", "0 1 2", "1 2", "0"), 0, 5)
        )
        insertQuestions(client, questions)
    }

    val lesson3 = allLessons.find { it.order == 3 }!!
    val existingQ3 = client.from("questions").select { filter { eq("lesson_id", lesson3.id) } }.decodeList<Question>()
    if (existingQ3.isEmpty()) {
        val questions = listOf(
            QuestionInsert(lesson3.id, "Qual palavra-chave indica que um método não retorna nenhum valor?", null, listOf("null", "void", "empty", "none"), 1, 1),
            QuestionInsert(lesson3.id, "O que é sobrecarga de métodos (overloading)?", null, listOf("Usar o mesmo nome com parâmetros diferentes", "Reutilizar um método de outra classe", "Chamar um método recursivamente", "Criar métodos estáticos"), 0, 2),
            QuestionInsert(lesson3.id, "Qual é o retorno deste método?", "public static int soma(int a, int b) {\n    return a + b;\n}\n// Chamada: soma(3, 7)", listOf("37", "10", "Erro", "null"), 1, 3),
            QuestionInsert(lesson3.id, "O que este método retorna?", "public static boolean ehPar(int n) {\n    return n % 2 == 0;\n}\n// Chamada: ehPar(5)", listOf("true", "false", "0", "Erro"), 1, 4),
            QuestionInsert(lesson3.id, "Quantas vezes a mensagem é impressa?", "public static void repetir(String msg, int vezes) {\n    for (int i = 0; i < vezes; i++) {\n        System.out.println(msg);\n    }\n}\n// Chamada: repetir(\"Oi\", 3)", listOf("1", "2", "3", "0"), 2, 5)
        )
        insertQuestions(client, questions)
    }

    val lesson4 = allLessons.find { it.order == 4 }!!
    val existingQ4 = client.from("questions").select { filter { eq("lesson_id", lesson4.id) } }.decodeList<Question>()
    if (existingQ4.isEmpty()) {
        val questions = listOf(
            QuestionInsert(lesson4.id, "Qual é o método especial chamado ao criar um objeto em Java?", null, listOf("init()", "create()", "construtor", "new()"), 2, 1),
            QuestionInsert(lesson4.id, "Qual modificador de acesso permite que apenas a própria classe acesse o atributo?", null, listOf("public", "protected", "private", "default"), 2, 2),
            QuestionInsert(lesson4.id, "O que significa herança em Java?", null, listOf("Uma classe herda atributos e métodos de outra", "Copiar código entre arquivos", "Criar variáveis globais", "Importar bibliotecas"), 0, 3),
            QuestionInsert(lesson4.id, "O que este código imprime?", "class Carro {\n    String modelo;\n    Carro(String modelo) {\n        this.modelo = modelo;\n    }\n}\nCarro c = new Carro(\"Civic\");\nSystem.out.println(c.modelo);", listOf("Carro", "Civic", "null", "Erro"), 1, 4),
            QuestionInsert(lesson4.id, "Qual é a saída?", "class Animal {\n    String falar() { return \"...\"; }\n}\nclass Gato extends Animal {\n    String falar() { return \"Miau\"; }\n}\nAnimal a = new Gato();\nSystem.out.println(a.falar());", listOf("...", "Miau", "Erro", "null"), 1, 5)
        )
        insertQuestions(client, questions)
    }
}

private suspend fun seedCourse2(client: SupabaseClient, courseId: Int) {
    val existingLessons = try { client.from("lessons").select { filter { eq("course_id", courseId) } }.decodeList<Lesson>() } catch(e: Exception) { emptyList() }
    val lessonsToInsert = listOf(
        LessonInsert(courseId, "Variáveis", "Entenda val, var e inferência de tipo em Kotlin", 1),
        LessonInsert(courseId, "Laços de Repetição", "Explore for, while e loops baseados em range", 2),
        LessonInsert(courseId, "Funções", "Funções, lambdas e extensões em Kotlin", 3),
        LessonInsert(courseId, "Classes", "Data classes, sealed classes e herança", 4)
    ).filter { newLesson -> existingLessons.none { it.order == newLesson.order } }

    val allLessons = existingLessons.toMutableList()
    if (lessonsToInsert.isNotEmpty()) {
        val insertedLessons = client.from("lessons").insert(lessonsToInsert) { select() }.decodeList<Lesson>()
        allLessons.addAll(insertedLessons)
    }

    val lesson5 = allLessons.find { it.order == 1 }!!
    val existingQ5 = client.from("questions").select { filter { eq("lesson_id", lesson5.id) } }.decodeList<Question>()
    if (existingQ5.isEmpty()) {
        val questions = listOf(
            QuestionInsert(lesson5.id, "Qual palavra-chave define uma variável imutável (constante) em Kotlin?", null, listOf("var", "final", "val", "const"), 2, 1),
            QuestionInsert(lesson5.id, "Como o Kotlin lida com valores nulos por padrão?", null, listOf("Permite null em qualquer tipo", "Tipos são não-nulos por padrão", "Usa Optional igual Java", "Ignora null"), 1, 2),
            QuestionInsert(lesson5.id, "O que é inferência de tipo em Kotlin?", null, listOf("O compilador deduz o tipo baseado no valor", "Tipos são dinâmicos", "Toda variável é Any", "Não existe tipagem forte"), 0, 3),
            QuestionInsert(lesson5.id, "Qual é o tipo da variável `x`?", "val x = 10.5", listOf("Int", "Double", "Float", "Number"), 1, 4),
            QuestionInsert(lesson5.id, "O que acontece neste código?", "var nome: String = \"Kotlin\"\nnome = null", listOf("Erro de compilação", "Compila normal", "Imprime null", "Crash"), 0, 5)
        )
        insertQuestions(client, questions)
    }

    val lesson6 = allLessons.find { it.order == 2 }!!
    val existingQ6 = client.from("questions").select { filter { eq("lesson_id", lesson6.id) } }.decodeList<Question>()
    if (existingQ6.isEmpty()) {
        val questions = listOf(
            QuestionInsert(lesson6.id, "Qual a sintaxe correta para iterar um range inclusivo?", null, listOf("for (i in 1..5)", "for (i = 1; i <= 5; i++)", "foreach (i : 1..5)", "loop 1 to 5"), 0, 1),
            QuestionInsert(lesson6.id, "Como fazer um loop decrescente?", null, listOf("downTo", "minus", "step -1", "reverse"), 0, 2),
            QuestionInsert(lesson6.id, "Quantas vezes imprime?", "for (i in 1..3) print(i)", listOf("2", "3", "4", "1"), 1, 3),
            QuestionInsert(lesson6.id, "Qual a saída?", "for (i in 1 until 4) print(i)", listOf("1234", "123", "12", "Erro"), 1, 4),
            QuestionInsert(lesson6.id, "O que imprime?", "var x = 3\nwhile(x > 0) {\n  print(x)\n  x--\n}", listOf("321", "3210", "210", "Infinito"), 0, 5)
        )
        insertQuestions(client, questions)
    }

    val lesson7 = allLessons.find { it.order == 3 }!!
    val existingQ7 = client.from("questions").select { filter { eq("lesson_id", lesson7.id) } }.decodeList<Question>()
    if (existingQ7.isEmpty()) {
        val questions = listOf(
            QuestionInsert(lesson7.id, "Qual palavra reservada inicia a declaração de função?", null, listOf("func", "def", "fun", "function"), 2, 1),
            QuestionInsert(lesson7.id, "O que é uma Single-Expression Function?", null, listOf("Uma função sem corpo entre chaves usando =", "Uma função lambda", "Uma função anônima", "Uma função privada"), 0, 2),
            QuestionInsert(lesson7.id, "O que retorna?", "fun soma(a: Int, b: Int) = a + b\nprintln(soma(2, 3))", listOf("23", "5", "Erro", "Unit"), 1, 3),
            QuestionInsert(lesson7.id, "Qual o tipo de retorno padrão se não especificado?", null, listOf("void", "null", "Unit", "Any"), 2, 4),
            QuestionInsert(lesson7.id, "O que este código faz?", "fun String.ola() = \"Olá \${'$'}this\"\nprintln(\"Mundo\".ola())", listOf("Olá Mundo", "Erro", "Mundo Ola", "Null"), 0, 5)
        )
        insertQuestions(client, questions)
    }

    val lesson8 = allLessons.find { it.order == 4 }!!
    val existingQ8 = client.from("questions").select { filter { eq("lesson_id", lesson8.id) } }.decodeList<Question>()
    if (existingQ8.isEmpty()) {
        val questions = listOf(
            QuestionInsert(lesson8.id, "Qual classe é usada automaticamente para conter dados?", null, listOf("struct", "record", "data class", "pojo"), 2, 1),
            QuestionInsert(lesson8.id, "Classes em Kotlin são por padrão...", null, listOf("final (fechadas)", "open (abertas)", "abstract", "static"), 0, 2),
            QuestionInsert(lesson8.id, "Como declarar um construtor primário?", null, listOf("class User(val nome: String)", "constructor User()", "def init()", "class User { init() }"), 0, 3),
            QuestionInsert(lesson8.id, "O que imprime?", "data class User(val nome: String)\nval u1 = User(\"Ana\")\nval u2 = User(\"Ana\")\nprintln(u1 == u2)", listOf("true", "false", "Erro", "Depende da memória"), 0, 4),
            QuestionInsert(lesson8.id, "Qual a saída?", "open class A\nclass B : A()\nprintln(B() is A)", listOf("true", "false", "Erro", "null"), 0, 5)
        )
        insertQuestions(client, questions)
    }
}

private suspend fun seedCourse3(client: SupabaseClient, courseId: Int) {
    val existingLessons = try { client.from("lessons").select { filter { eq("course_id", courseId) } }.decodeList<Lesson>() } catch(e: Exception) { emptyList() }
    val lessonsToInsert = listOf(
        LessonInsert(courseId, "Variáveis", "Tipagem dinâmica, atribuições e tipos de dados", 1),
        LessonInsert(courseId, "Laços de Repetição", "for-in, while e list comprehensions", 2),
        LessonInsert(courseId, "Funções", "def, parâmetros padrão, *args e **kwargs", 3),
        LessonInsert(courseId, "Classes", "POO em Python, __init__, herança e métodos especiais", 4)
    ).filter { newLesson -> existingLessons.none { it.order == newLesson.order } }

    val allLessons = existingLessons.toMutableList()
    if (lessonsToInsert.isNotEmpty()) {
        val insertedLessons = client.from("lessons").insert(lessonsToInsert) { select() }.decodeList<Lesson>()
        allLessons.addAll(insertedLessons)
    }

    val lesson9 = allLessons.find { it.order == 1 }!!
    val existingQ9 = client.from("questions").select { filter { eq("lesson_id", lesson9.id) } }.decodeList<Question>()
    if (existingQ9.isEmpty()) {
        val questions = listOf(
            QuestionInsert(lesson9.id, "Python necessita de declaração explícita de tipos?", null, listOf("Sim", "Não, é dinâmico", "Apenas para strings", "Depende da versão"), 1, 1),
            QuestionInsert(lesson9.id, "Como verificar o tipo de uma variável?", null, listOf("typeof(x)", "type(x)", "instanceof(x)", "check(x)"), 1, 2),
            QuestionInsert(lesson9.id, "Qual o valor de x?", "x = 10\nx = \"texto\"\nprint(x)", listOf("10", "texto", "Erro de tipo", "null"), 1, 3),
            QuestionInsert(lesson9.id, "Como se cria uma lista vazia?", null, listOf("list() ou []", "new List()", "Array()", "{}"), 0, 4),
            QuestionInsert(lesson9.id, "O que imprime?", "a = [1, 2, 3]\nb = a\nb.append(4)\nprint(len(a))", listOf("3", "4", "Erro", "0"), 1, 5)
        )
        insertQuestions(client, questions)
    }

    val lesson10 = allLessons.find { it.order == 2 }!!
    val existingQ10 = client.from("questions").select { filter { eq("lesson_id", lesson10.id) } }.decodeList<Question>()
    if (existingQ10.isEmpty()) {
        val questions = listOf(
            QuestionInsert(lesson10.id, "Qual função gera uma sequência de números?", null, listOf("seq()", "range()", "xrange()", "list()"), 1, 1),
            QuestionInsert(lesson10.id, "Como interromper um loop imediatamente?", null, listOf("stop", "exit", "break", "halt"), 2, 2),
            QuestionInsert(lesson10.id, "Quantas vezes imprime?", "for i in range(5):\n    print(i)", listOf("4", "5", "6", "1"), 1, 3),
            QuestionInsert(lesson10.id, "Qual a saída?", "x = [i*2 for i in range(3)]\nprint(x)", listOf("[0, 1, 2]", "[0, 2, 4]", "[2, 4, 6]", "[1, 2, 3]"), 1, 4),
            QuestionInsert(lesson10.id, "O que imprime?", "i = 0\nwhile i < 3:\n    print(i, end=\"\")\n    i += 1", listOf("0 1 2", "012", "123", "01"), 1, 5)
        )
        insertQuestions(client, questions)
    }

    val lesson11 = allLessons.find { it.order == 3 }!!
    val existingQ11 = client.from("questions").select { filter { eq("lesson_id", lesson11.id) } }.decodeList<Question>()
    if (existingQ11.isEmpty()) {
        val questions = listOf(
            QuestionInsert(lesson11.id, "Qual palavra define uma função em Python?", null, listOf("func", "fun", "function", "def"), 3, 1),
            QuestionInsert(lesson11.id, "Como definir valor padrão para parâmetro?", null, listOf("def f(p: 0)", "def f(p=0)", "def f(p == 0)", "def f(p -> 0)"), 1, 2),
            QuestionInsert(lesson11.id, "O que retorna?", "def soma(a, b):\n    return a + b\nprint(soma(\"Oj\", \"a\"))", listOf("Oja", "Erro", "NaN", "null"), 0, 3),
            QuestionInsert(lesson11.id, "Qual a saída?", "def f(x=[]):\n    x.append(1)\n    return x\nprint(f()); print(f())", listOf("[1] [1]", "[1] [1, 1]", "[1] []", "Erro"), 1, 4),
            QuestionInsert(lesson11.id, "O que args captura?", "def f(*args):\n    print(type(args))", listOf("list", "tuple", "dict", "set"), 1, 5)
        )
        insertQuestions(client, questions)
    }

    val lesson12 = allLessons.find { it.order == 4 }!!
    val existingQ12 = client.from("questions").select { filter { eq("lesson_id", lesson12.id) } }.decodeList<Question>()
    if (existingQ12.isEmpty()) {
        val questions = listOf(
            QuestionInsert(lesson12.id, "Qual é o nome do método construtor?", null, listOf("constructor", "__init__", "init", "new"), 1, 1),
            QuestionInsert(lesson12.id, "O que representa o primeiro parâmetro de métodos (self)?", null, listOf("A instância atual", "A classe", "O módulo", "Nada"), 0, 2),
            QuestionInsert(lesson12.id, "Como indicar herança?", null, listOf("class A(B):", "class A extends B:", "class A : B", "class A inherits B"), 0, 3),
            QuestionInsert(lesson12.id, "Qual a saída?", "class Cao:\n    kind = \"canino\"\nc = Cao()\nc.kind = \"lobo\"\nprint(Cao.kind)", listOf("lobo", "canino", "Erro", "null"), 1, 4),
            QuestionInsert(lesson12.id, "O que imprime?", "class A:\n    def __str__(self): return \"A\"\nprint(A())", listOf("<Object A>", "A", "Endereço de memória", "Erro"), 1, 5)
        )
        insertQuestions(client, questions)
    }
}

private suspend fun seedCourse4(client: SupabaseClient, courseId: Int) {
    val existingLessons = try { client.from("lessons").select { filter { eq("course_id", courseId) } }.decodeList<Lesson>() } catch(e: Exception) { emptyList() }
    val lessonsToInsert = listOf(
        LessonInsert(courseId, "Variáveis", "let, const, anotações de tipo e inferência", 1),
        LessonInsert(courseId, "Laços de Repetição", "for, for-of, while e métodos de array", 2),
        LessonInsert(courseId, "Funções", "Funções tipadas, arrow functions e generics", 3),
        LessonInsert(courseId, "Classes", "Classes, interfaces e modificadores de acesso", 4)
    ).filter { newLesson -> existingLessons.none { it.order == newLesson.order } }

    val allLessons = existingLessons.toMutableList()
    if (lessonsToInsert.isNotEmpty()) {
        val insertedLessons = client.from("lessons").insert(lessonsToInsert) { select() }.decodeList<Lesson>()
        allLessons.addAll(insertedLessons)
    }

    val lesson13 = allLessons.find { it.order == 1 }!!
    val existingQ13 = client.from("questions").select { filter { eq("lesson_id", lesson13.id) } }.decodeList<Question>()
    if (existingQ13.isEmpty()) {
        val questions = listOf(
            QuestionInsert(lesson13.id, "Qual é o superset que TypeScript estende?", null, listOf("Java", "C#", "JavaScript", "Python"), 2, 1),
            QuestionInsert(lesson13.id, "Como tipar explicitamente uma variável numérica?", null, listOf("let n: number", "let n: int", "var n = (int)", "const n :: Number"), 0, 2),
            QuestionInsert(lesson13.id, "Qual tipo representa \"qualquer coisa\"?", null, listOf("Object", "void", "any", "unknown"), 2, 3),
            QuestionInsert(lesson13.id, "O que acontece?", "let x: string = \"TS\";\nx = 10;", listOf("Erro de compilação TS", "Funciona pois é JS", "Apenas warning", "Crash"), 0, 4),
            QuestionInsert(lesson13.id, "Qual a saída?", "const lista: number[] = [1, 2];\nlista.push(\"3\");", listOf("[1, 2, '3']", "Erro de tipo", "NaN", "null"), 1, 5)
        )
        insertQuestions(client, questions)
    }

    val lesson14 = allLessons.find { it.order == 2 }!!
    val existingQ14 = client.from("questions").select { filter { eq("lesson_id", lesson14.id) } }.decodeList<Question>()
    if (existingQ14.isEmpty()) {
        val questions = listOf(
            QuestionInsert(lesson14.id, "Qual loop itera sobre valores de array?", null, listOf("for..in", "for..of", "foreach", "loop"), 1, 1),
            QuestionInsert(lesson14.id, "Qual método de array cria um novo array transformado?", null, listOf("forEach", "map", "filter", "reduce"), 1, 2),
            QuestionInsert(lesson14.id, "O que imprime?", "const a = [10, 20];\nfor (let i of a) console.log(i);", listOf("0 1", "10 20", "undefined", "Erro"), 1, 3),
            QuestionInsert(lesson14.id, "Qual a saída?", "let i = 0;\ndo { i++; } while (i < 0);\nconsole.log(i);", listOf("0", "1", "Erro", "-1"), 1, 4),
            QuestionInsert(lesson14.id, "O que filter faz?", "const nums = [1, 2, 3, 4];\nconsole.log(nums.filter(n => n % 2 === 0));", listOf("[1, 3]", "[2, 4]", "[true, false, true, false]", "[2]"), 1, 5)
        )
        insertQuestions(client, questions)
    }

    val lesson15 = allLessons.find { it.order == 3 }!!
    val existingQ15 = client.from("questions").select { filter { eq("lesson_id", lesson15.id) } }.decodeList<Question>()
    if (existingQ15.isEmpty()) {
        val questions = listOf(
            QuestionInsert(lesson15.id, "Como definir retorno opcional?", null, listOf("func f(): void?", "func f()?", "Retorno é sempre obrigatório", "void | undefined"), 3, 1),
            QuestionInsert(lesson15.id, "Sintaxe de Arrow Function:", null, listOf("=>", "->", "function", "def"), 0, 2),
            QuestionInsert(lesson15.id, "O que este código retorna?", "const soma = (a: number, b: number) => a + b;\nconsole.log(soma(2, \"2\"));", listOf("4", "22", "Erro de tipo", "NaN"), 2, 3),
            QuestionInsert(lesson15.id, "Qual o tipo de retorno?", "function nada(): void { return 1; }", listOf("number", "void", "Erro TS", "undefined"), 2, 4),
            QuestionInsert(lesson15.id, "Generics servem para...", null, listOf("Criar componentes reutilizáveis e tipados", "Aumentar performance", "Ofuscar código", "Validar em runtime"), 0, 5)
        )
        insertQuestions(client, questions)
    }

    val lesson16 = allLessons.find { it.order == 4 }!!
    val existingQ16 = client.from("questions").select { filter { eq("lesson_id", lesson16.id) } }.decodeList<Question>()
    if (existingQ16.isEmpty()) {
        val questions = listOf(
            QuestionInsert(lesson16.id, "Qual modificador deixa o atributo acessível apenas na classe?", null, listOf("public", "static", "private", "readonly"), 2, 1),
            QuestionInsert(lesson16.id, "O que é uma interface em TS?", null, listOf("Um contrato de estrutura de objeto", "Uma classe compilada", "Uma função global", "Uma variável"), 0, 2),
            QuestionInsert(lesson16.id, "Como implementar uma interface?", null, listOf("class A implements I", "class A extends I", "class A inherits I", "class A : I"), 0, 3),
            QuestionInsert(lesson16.id, "O que imprime?", "class A { static x = 10 }\nconsole.log(A.x)", listOf("undefined", "10", "Erro", "null"), 1, 4),
            QuestionInsert(lesson16.id, "Qual a saída?", "interface User { name: string }\nconst u: User = { name: 10 };", listOf("{ name: 10 }", "Erro de tipo", "null", "undefined"), 1, 5)
        )
        insertQuestions(client, questions)
    }
}

