package com.plinkoria.game.content

/*
 * ============================================================================
 *  QUIZ CONTENT — theme: circles, balls & spheres. REPLACEABLE.
 *  Three difficulty levels × 10 questions, 4 options each, exactly one correct.
 *  Keep `correctIndex` 0-based and valid. The `id`s key saved best scores —
 *  keep them stable once shipped.
 * ============================================================================
 */

data class QuizQuestion(
    val text: String,
    val options: List<String>,
    val correctIndex: Int
)

data class QuizCategory(
    val id: String,
    /** 1 = Easy, 2 = Medium, 3 = Hard — drives stars and artwork. */
    val difficulty: Int,
    val title: String,
    val subtitle: String,
    val questions: List<QuizQuestion>
)

object QuizContent {

    const val OPTIONS_PER_QUESTION = 4

    val categories: List<QuizCategory> = listOf(
        QuizCategory(
            id = "easy",
            difficulty = 1,
            title = "Easy",
            subtitle = "Bouncy Basics",
            questions = listOf(
                QuizQuestion("What is the distance from the centre of a circle to its edge called?", listOf("Radius", "Diameter", "Chord", "Arc"), 0),
                QuizQuestion("What is a perfectly round 3D ball shape called?", listOf("Cube", "Sphere", "Cone", "Cylinder"), 1),
                QuizQuestion("A classic soccer ball is stitched from pentagons and which other shape?", listOf("Squares", "Triangles", "Hexagons", "Octagons"), 2),
                QuizQuestion("Which of these sports uses the smallest ball?", listOf("Basketball", "Volleyball", "Football", "Golf"), 3),
                QuizQuestion("Which planet is the biggest 'ball' in our Solar System?", listOf("Jupiter", "Saturn", "Earth", "Neptune"), 0),
                QuizQuestion("In snooker, which ball does the player hit with the cue?", listOf("The black ball", "The white cue ball", "A red ball", "The pink ball"), 1),
                QuizQuestion("A circle's diameter is how many times its radius?", listOf("Half", "3 times", "2 times", "4 times"), 2),
                QuizQuestion("How many numbered object balls are racked in a game of 8-ball pool?", listOf("9", "10", "12", "15"), 3),
                QuizQuestion("Which sport is played with an oval ball rather than a round one?", listOf("Rugby", "Tennis", "Bowling", "Basketball"), 0),
                QuizQuestion("What colour are most official tennis balls?", listOf("White", "Yellow", "Orange", "Red"), 1)
            )
        ),
        QuizCategory(
            id = "medium",
            difficulty = 2,
            title = "Medium",
            subtitle = "Round Science",
            questions = listOf(
                QuizQuestion("What is the value of π (pi), rounded to two decimals?", listOf("3.14", "2.72", "1.62", "3.41"), 0),
                QuizQuestion("Which formula gives the circumference of a circle?", listOf("πr²", "2πr", "πd²", "r²/2"), 1),
                QuizQuestion("Which formula gives the area of a circle?", listOf("2πr", "πd", "πr²", "4πr"), 2),
                QuizQuestion("How many degrees are there in a full circle?", listOf("90°", "180°", "270°", "360°"), 3),
                QuizQuestion("A straight line that touches a circle at exactly one point is a…", listOf("Tangent", "Chord", "Secant", "Radius"), 0),
                QuizQuestion("Why do free-floating soap bubbles form spheres?", listOf("Gravity pulls them round", "Surface tension minimises their surface", "Air inside is heavier", "Wind shapes them"), 1),
                QuizQuestion("What shape are the orbits of the planets, according to Kepler?", listOf("Perfect circles", "Spirals", "Ellipses", "Squares"), 2),
                QuizQuestion("Which solid has a circular base and comes to a single point?", listOf("Sphere", "Cylinder", "Prism", "Cone"), 3),
                QuizQuestion("Which ball size is used in men's professional basketball?", listOf("Size 7", "Size 5", "Size 3", "Size 9"), 0),
                QuizQuestion("Which planet is the most flattened 'ball', bulging at its equator?", listOf("Mercury", "Saturn", "Venus", "Mars"), 1)
            )
        ),
        QuizCategory(
            id = "hard",
            difficulty = 3,
            title = "Hard",
            subtitle = "Sphere Masters",
            questions = listOf(
                QuizQuestion("Which formula gives the volume of a sphere?", listOf("4/3 πr³", "4πr²", "πr²h", "2πr³"), 0),
                QuizQuestion("Which formula gives the surface area of a sphere?", listOf("2πr²", "4πr²", "4/3 πr³", "πr²"), 1),
                QuizQuestion("How many radians make one full turn of a circle?", listOf("π", "360", "2π", "4π"), 2),
                QuizQuestion("The circle passing through all three corners of a triangle is the…", listOf("Incircle", "Excircle", "Unit circle", "Circumcircle"), 3),
                QuizQuestion("Which device, invented by Francis Galton, drops balls through rows of pegs?", listOf("Galton board", "Newton's cradle", "Orrery", "Abacus"), 0),
                QuizQuestion("Balls falling through a peg board pile up into which shape of distribution?", listOf("Uniform (flat)", "Bell curve (normal)", "Exponential", "Zig-zag"), 1),
                QuizQuestion("On which TV game show did the Plinko board first appear?", listOf("Wheel of Fortune", "Jeopardy!", "The Price Is Right", "Family Feud"), 2),
                QuizQuestion("Pachinko, a pinball-style ball game, is hugely popular in which country?", listOf("Germany", "Brazil", "Canada", "Japan"), 3),
                QuizQuestion("Roughly how many dimples does a typical golf ball have?", listOf("About 350", "About 30", "About 100", "About 2,000"), 0),
                QuizQuestion("Besides momentum, what does Newton's cradle show being conserved?", listOf("Mass only", "Energy", "Electric charge", "Temperature"), 1)
            )
        )
    )

    fun category(id: String?): QuizCategory? =
        if (id.isNullOrBlank()) null else categories.firstOrNull { it.id == id }
}
