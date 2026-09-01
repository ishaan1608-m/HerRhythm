package com.herrhythm.app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FitnessRepository {

    // ─────────────────────────────────────────────
    // EXERCISE LIBRARY
    // ─────────────────────────────────────────────

    private fun squats(durationSec: Int = 40) = Exercise(
        id = "squat", name = "Squats", type = ExerciseType.SQUAT,
        targetMuscles = listOf(MuscleGroup.LEGS, MuscleGroup.GLUTES),
        durationSeconds = durationSec, restAfterSeconds = 15,
        instructions = listOf(
            "Stand with feet shoulder-width apart",
            "Keep your back straight and chest up",
            "Lower your body until thighs are parallel to floor",
            "Push through your heels to return to start",
            "Breathe in on the way down, out on the way up"
        ),
        tips = "Keep your knees behind your toes",
        caloriesBurnedEstimate = 6
    )

    private fun pushUps(durationSec: Int = 30) = Exercise(
        id = "pushup", name = "Push-Ups", type = ExerciseType.PUSH_UP,
        targetMuscles = listOf(MuscleGroup.UPPER_BODY, MuscleGroup.CORE),
        durationSeconds = durationSec, restAfterSeconds = 15,
        instructions = listOf(
            "Start in a high plank with hands slightly wider than shoulders",
            "Keep your body in a straight line head to toe",
            "Lower your chest toward the floor",
            "Push back up to start position",
            "Modify on knees if needed"
        ),
        tips = "Don't let your hips sag or pike up",
        caloriesBurnedEstimate = 7
    )

    private fun plank(durationSec: Int = 30) = Exercise(
        id = "plank", name = "Plank Hold", type = ExerciseType.PLANK,
        targetMuscles = listOf(MuscleGroup.CORE, MuscleGroup.FULL_BODY),
        durationSeconds = durationSec, restAfterSeconds = 10,
        instructions = listOf(
            "Place forearms on floor, elbows under shoulders",
            "Extend legs behind you, resting on toes",
            "Keep your body in a straight line",
            "Engage your core — imagine bracing for a punch",
            "Breathe steadily and hold"
        ),
        tips = "Squeeze your glutes for extra stability",
        caloriesBurnedEstimate = 4
    )

    private fun jumpingJacks(durationSec: Int = 45) = Exercise(
        id = "jumping_jack", name = "Jumping Jacks", type = ExerciseType.JUMPING_JACK,
        targetMuscles = listOf(MuscleGroup.FULL_BODY, MuscleGroup.CARDIO),
        durationSeconds = durationSec, restAfterSeconds = 10,
        instructions = listOf(
            "Stand with feet together, arms at sides",
            "Jump and spread feet wider than hip-width",
            "Simultaneously raise arms overhead",
            "Jump back to starting position",
            "Maintain a steady rhythm"
        ),
        tips = "Land softly to protect your joints",
        caloriesBurnedEstimate = 8
    )

    private fun highKnees(durationSec: Int = 40) = Exercise(
        id = "high_knees", name = "High Knees", type = ExerciseType.HIGH_KNEES,
        targetMuscles = listOf(MuscleGroup.LEGS, MuscleGroup.CARDIO, MuscleGroup.CORE),
        durationSeconds = durationSec, restAfterSeconds = 15,
        instructions = listOf(
            "Stand with feet hip-width apart",
            "Run in place, bringing knees up to hip height",
            "Pump your arms in opposition to your legs",
            "Land lightly on the balls of your feet",
            "Keep your core tight throughout"
        ),
        tips = "Keep your posture tall — don't lean back",
        caloriesBurnedEstimate = 9
    )

    private fun mountainClimbers(durationSec: Int = 30) = Exercise(
        id = "mountain_climber", name = "Mountain Climbers", type = ExerciseType.MOUNTAIN_CLIMBER,
        targetMuscles = listOf(MuscleGroup.CORE, MuscleGroup.FULL_BODY, MuscleGroup.CARDIO),
        durationSeconds = durationSec, restAfterSeconds = 15,
        instructions = listOf(
            "Start in a high plank position",
            "Drive your right knee toward your chest",
            "Quickly switch legs — left knee in, right leg back",
            "Keep your hips level and core tight",
            "Maintain a fast but controlled pace"
        ),
        tips = "Keep your shoulders stacked over wrists",
        caloriesBurnedEstimate = 10
    )

    private fun lunges(durationSec: Int = 40) = Exercise(
        id = "lunge", name = "Alternating Lunges", type = ExerciseType.LUNGE,
        targetMuscles = listOf(MuscleGroup.LEGS, MuscleGroup.GLUTES),
        durationSeconds = durationSec, restAfterSeconds = 15,
        instructions = listOf(
            "Stand with feet hip-width apart",
            "Step forward with your right foot",
            "Lower your hips until both knees are at 90 degrees",
            "Push off front foot to return to standing",
            "Alternate legs with each rep"
        ),
        tips = "Keep your front knee above your ankle",
        caloriesBurnedEstimate = 6
    )

    private fun bicycleCrunch(durationSec: Int = 30) = Exercise(
        id = "bicycle_crunch", name = "Bicycle Crunches", type = ExerciseType.BICYCLE_CRUNCH,
        targetMuscles = listOf(MuscleGroup.CORE),
        durationSeconds = durationSec, restAfterSeconds = 15,
        instructions = listOf(
            "Lie on your back, hands behind your head",
            "Lift shoulders off the floor",
            "Bring right knee to chest, extend left leg",
            "Twist left elbow toward right knee",
            "Alternate sides in a cycling motion"
        ),
        tips = "Focus on rotation, not just bringing elbow to knee",
        caloriesBurnedEstimate = 5
    )

    private fun gluteBridge(durationSec: Int = 35) = Exercise(
        id = "glute_bridge", name = "Glute Bridge", type = ExerciseType.GLUTE_BRIDGE,
        targetMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.CORE),
        durationSeconds = durationSec, restAfterSeconds = 10,
        instructions = listOf(
            "Lie on your back, knees bent, feet flat on floor",
            "Place arms at sides, palms down",
            "Push through heels to lift hips toward ceiling",
            "Squeeze glutes at the top",
            "Lower back down slowly"
        ),
        tips = "Drive through your heels, not your toes",
        caloriesBurnedEstimate = 4
    )

    private fun jumpSquat(durationSec: Int = 30) = Exercise(
        id = "jump_squat", name = "Jump Squats", type = ExerciseType.JUMP_SQUAT,
        targetMuscles = listOf(MuscleGroup.LEGS, MuscleGroup.GLUTES, MuscleGroup.CARDIO),
        durationSeconds = durationSec, restAfterSeconds = 20,
        instructions = listOf(
            "Start in squat position",
            "Explode upward through your heels",
            "Fully extend your body at the top",
            "Land softly back into squat position",
            "Absorb the landing with bent knees"
        ),
        tips = "Land as quietly as possible to protect knees",
        caloriesBurnedEstimate = 12
    )

    private fun burpee(durationSec: Int = 35) = Exercise(
        id = "burpee", name = "Burpees", type = ExerciseType.BURPEE,
        targetMuscles = listOf(MuscleGroup.FULL_BODY, MuscleGroup.CARDIO),
        durationSeconds = durationSec, restAfterSeconds = 20,
        instructions = listOf(
            "Start standing, then squat and place hands on floor",
            "Jump feet back into plank position",
            "Perform a push-up",
            "Jump feet back to hands",
            "Jump up with arms overhead"
        ),
        tips = "Modify by stepping instead of jumping",
        caloriesBurnedEstimate = 14
    )

    private fun catCow(durationSec: Int = 45) = Exercise(
        id = "cat_cow", name = "Cat-Cow Stretch", type = ExerciseType.CAT_COW,
        targetMuscles = listOf(MuscleGroup.BACK, MuscleGroup.FLEXIBILITY),
        durationSeconds = durationSec, restAfterSeconds = 5,
        instructions = listOf(
            "Start on all fours, wrists under shoulders",
            "Inhale: drop belly, lift chest and tailbone (Cow)",
            "Exhale: round spine toward ceiling, tuck chin (Cat)",
            "Move slowly with your breath",
            "Repeat for the full duration"
        ),
        tips = "Let your breath guide the movement",
        caloriesBurnedEstimate = 2
    )

    private fun downwardDog(durationSec: Int = 40) = Exercise(
        id = "downward_dog", name = "Downward Dog", type = ExerciseType.DOWNWARD_DOG,
        targetMuscles = listOf(MuscleGroup.FULL_BODY, MuscleGroup.FLEXIBILITY),
        durationSeconds = durationSec, restAfterSeconds = 5,
        instructions = listOf(
            "Start on all fours, tuck toes under",
            "Push hips up and back, forming an inverted V",
            "Straighten arms and legs as much as comfortable",
            "Press heels toward the floor",
            "Hold and breathe deeply"
        ),
        tips = "Bend knees slightly if hamstrings are tight",
        caloriesBurnedEstimate = 3
    )

    private fun childPose(durationSec: Int = 40) = Exercise(
        id = "child_pose", name = "Child's Pose", type = ExerciseType.CHILD_POSE,
        targetMuscles = listOf(MuscleGroup.BACK, MuscleGroup.FLEXIBILITY),
        durationSeconds = durationSec, restAfterSeconds = 0,
        instructions = listOf(
            "Kneel on the floor, big toes touching",
            "Sit back on your heels",
            "Extend arms forward on the floor",
            "Rest your forehead on the mat",
            "Breathe deeply into your back"
        ),
        tips = "This is a rest pose — surrender fully",
        caloriesBurnedEstimate = 1
    )

    private fun cobraPose(durationSec: Int = 35) = Exercise(
        id = "cobra", name = "Cobra Pose", type = ExerciseType.COBRA,
        targetMuscles = listOf(MuscleGroup.BACK, MuscleGroup.SHOULDERS),
        durationSeconds = durationSec, restAfterSeconds = 5,
        instructions = listOf(
            "Lie face down, hands under shoulders",
            "Press tops of feet into the floor",
            "Inhale and press up, lifting chest",
            "Keep elbows slightly bent, shoulders relaxed",
            "Hold and breathe"
        ),
        tips = "Don't fully lock out your elbows",
        caloriesBurnedEstimate = 2
    )

    private fun warriorI(durationSec: Int = 35) = Exercise(
        id = "warrior_i", name = "Warrior I", type = ExerciseType.WARRIOR_I,
        targetMuscles = listOf(MuscleGroup.LEGS, MuscleGroup.FLEXIBILITY, MuscleGroup.SHOULDERS),
        durationSeconds = durationSec, restAfterSeconds = 5,
        instructions = listOf(
            "Step right foot forward, left foot angled 45°",
            "Bend right knee to 90 degrees over ankle",
            "Keep hips facing forward",
            "Raise arms overhead, palms facing each other",
            "Hold, then switch sides"
        ),
        tips = "Keep back heel firmly on the ground",
        caloriesBurnedEstimate = 3
    )

    private fun russianTwist(durationSec: Int = 30) = Exercise(
        id = "russian_twist", name = "Russian Twists", type = ExerciseType.RUSSIAN_TWIST,
        targetMuscles = listOf(MuscleGroup.CORE),
        durationSeconds = durationSec, restAfterSeconds = 15,
        instructions = listOf(
            "Sit on floor, knees bent, feet slightly lifted",
            "Lean back at 45 degrees, keep spine straight",
            "Clasp hands in front of you",
            "Rotate torso to tap floor beside left hip",
            "Rotate right and repeat"
        ),
        tips = "Move from your core, not your arms",
        caloriesBurnedEstimate = 5
    )

    private fun legRaises(durationSec: Int = 30) = Exercise(
        id = "leg_raise", name = "Leg Raises", type = ExerciseType.LEG_RAISE,
        targetMuscles = listOf(MuscleGroup.CORE, MuscleGroup.LEGS),
        durationSeconds = durationSec, restAfterSeconds = 15,
        instructions = listOf(
            "Lie flat on back, hands under hips",
            "Keep legs straight, together",
            "Raise legs to 90 degrees",
            "Lower slowly without touching floor",
            "Repeat with control"
        ),
        tips = "Press lower back into the floor",
        caloriesBurnedEstimate = 5
    )

    private fun sidePlank(durationSec: Int = 25) = Exercise(
        id = "side_plank", name = "Side Plank", type = ExerciseType.SIDE_PLANK,
        targetMuscles = listOf(MuscleGroup.CORE),
        durationSeconds = durationSec, restAfterSeconds = 10,
        instructions = listOf(
            "Lie on your right side, legs stacked",
            "Place right forearm on floor, elbow under shoulder",
            "Lift hips to form a straight line",
            "Keep body rigid, don't let hips drop",
            "Hold, then repeat on left side"
        ),
        tips = "Stack your feet or stagger for easier balance",
        caloriesBurnedEstimate = 4
    )

    private fun sprintInPlace(durationSec: Int = 20) = Exercise(
        id = "sprint_in_place", name = "Sprint in Place", type = ExerciseType.SPRINT_IN_PLACE,
        targetMuscles = listOf(MuscleGroup.FULL_BODY, MuscleGroup.CARDIO),
        durationSeconds = durationSec, restAfterSeconds = 30,
        instructions = listOf(
            "Run as fast as possible in place",
            "Drive knees high",
            "Pump arms aggressively",
            "Stay on balls of your feet",
            "Give maximum effort for the full duration"
        ),
        tips = "This is your sprint — go all out!",
        caloriesBurnedEstimate = 15
    )

    // ─────────────────────────────────────────────
    // PROGRAMS
    // ─────────────────────────────────────────────

    private fun buildWeightLossSessions(): List<WorkoutSession> = listOf(
        WorkoutSession(
            id = "wl_d1", dayNumber = 1, title = "Day 1 — Fat Burn Kickstart",
            exercises = listOf(jumpingJacks(50), highKnees(40), squats(40), mountainClimbers(30), lunges(40), gluteBridge(35), plank(30), bicycleCrunch(30)),
            totalDurationMin = 30, estimatedCalories = 280, intensity = WorkoutIntensity.HIGH, focusArea = "Full Body Cardio"
        ),
        WorkoutSession(
            id = "wl_d2", dayNumber = 2, title = "Day 2 — Lower Body Burn",
            exercises = listOf(highKnees(50), jumpSquat(30), lunges(45), squats(45), gluteBridge(40), legRaises(30), sidePlank(25), mountainClimbers(30)),
            totalDurationMin = 30, estimatedCalories = 270, intensity = WorkoutIntensity.HIGH, focusArea = "Legs & Glutes"
        ),
        WorkoutSession(
            id = "wl_d3", dayNumber = 3, title = "Day 3 — Core & Cardio",
            exercises = listOf(jumpingJacks(45), plank(35), bicycleCrunch(30), russianTwist(30), mountainClimbers(30), legRaises(30), highKnees(40), burpee(35)),
            totalDurationMin = 30, estimatedCalories = 260, intensity = WorkoutIntensity.HIGH, focusArea = "Core & Cardio"
        ),
        WorkoutSession(
            id = "wl_d4", dayNumber = 4, title = "Day 4 — Upper & Core",
            exercises = listOf(jumpingJacks(40), pushUps(30), mountainClimbers(30), plank(30), bicycleCrunch(30), highKnees(40), pushUps(25), russianTwist(30)),
            totalDurationMin = 28, estimatedCalories = 240, intensity = WorkoutIntensity.MODERATE, focusArea = "Upper Body & Core"
        ),
        WorkoutSession(
            id = "wl_d5", dayNumber = 5, title = "Day 5 — HIIT Blast",
            exercises = listOf(sprintInPlace(20), jumpSquat(30), burpee(35), highKnees(40), jumpingJacks(40), mountainClimbers(30), sprintInPlace(20), plank(30)),
            totalDurationMin = 28, estimatedCalories = 320, intensity = WorkoutIntensity.HIIT, focusArea = "Maximum Calorie Burn"
        ),
        WorkoutSession(
            id = "wl_d6", dayNumber = 6, title = "Day 6 — Full Body Finisher",
            exercises = listOf(jumpingJacks(40), squats(40), pushUps(30), lunges(40), mountainClimbers(30), gluteBridge(35), plank(30), jumpSquat(25)),
            totalDurationMin = 30, estimatedCalories = 290, intensity = WorkoutIntensity.HIGH, focusArea = "Full Body"
        )
    )

    private fun buildYogaSessions(): List<WorkoutSession> = listOf(
        WorkoutSession(
            id = "yoga_d1", dayNumber = 1, title = "Day 1 — Morning Flow",
            exercises = listOf(catCow(50), downwardDog(45), cobraPose(40), warriorI(40), childPose(45), catCow(40)),
            totalDurationMin = 25, estimatedCalories = 90, intensity = WorkoutIntensity.EASY, focusArea = "Spine & Flexibility"
        ),
        WorkoutSession(
            id = "yoga_d2", dayNumber = 2, title = "Day 2 — Hip & Hamstring",
            exercises = listOf(downwardDog(50), warriorI(40), lunges(40), childPose(40), cobraPose(35), catCow(40)),
            totalDurationMin = 25, estimatedCalories = 100, intensity = WorkoutIntensity.EASY, focusArea = "Hips & Hamstrings"
        ),
        WorkoutSession(
            id = "yoga_d3", dayNumber = 3, title = "Day 3 — Core & Balance",
            exercises = listOf(catCow(40), plank(30), sidePlank(25), downwardDog(40), warriorI(35), childPose(45)),
            totalDurationMin = 23, estimatedCalories = 110, intensity = WorkoutIntensity.MODERATE, focusArea = "Core & Balance"
        ),
        WorkoutSession(
            id = "yoga_d4", dayNumber = 4, title = "Day 4 — Full Body Restore",
            exercises = listOf(catCow(50), downwardDog(50), cobraPose(45), warriorI(40), childPose(50)),
            totalDurationMin = 25, estimatedCalories = 85, intensity = WorkoutIntensity.EASY, focusArea = "Restoration"
        ),
        WorkoutSession(
            id = "yoga_d5", dayNumber = 5, title = "Day 5 — Strength Flow",
            exercises = listOf(downwardDog(40), squats(35), pushUps(25), warriorI(40), plank(30), catCow(40), childPose(40)),
            totalDurationMin = 25, estimatedCalories = 140, intensity = WorkoutIntensity.MODERATE, focusArea = "Strength + Yoga"
        )
    )

    private fun buildStrengthSessions(): List<WorkoutSession> = listOf(
        WorkoutSession(
            id = "str_d1", dayNumber = 1, title = "Day 1 — Lower Body Power",
            exercises = listOf(squats(45), lunges(45), gluteBridge(40), jumpSquat(30), sidePlank(25), legRaises(30)),
            totalDurationMin = 35, estimatedCalories = 220, intensity = WorkoutIntensity.MODERATE, focusArea = "Legs & Glutes"
        ),
        WorkoutSession(
            id = "str_d2", dayNumber = 2, title = "Day 2 — Upper Body Focus",
            exercises = listOf(pushUps(35), plank(35), mountainClimbers(30), pushUps(30), sidePlank(25), bicycleCrunch(30)),
            totalDurationMin = 32, estimatedCalories = 190, intensity = WorkoutIntensity.MODERATE, focusArea = "Upper Body"
        ),
        WorkoutSession(
            id = "str_d3", dayNumber = 3, title = "Day 3 — Core Blast",
            exercises = listOf(plank(40), bicycleCrunch(35), russianTwist(30), legRaises(35), sidePlank(25), mountainClimbers(30)),
            totalDurationMin = 30, estimatedCalories = 180, intensity = WorkoutIntensity.MODERATE, focusArea = "Core"
        ),
        WorkoutSession(
            id = "str_d4", dayNumber = 4, title = "Day 4 — Full Body Strength",
            exercises = listOf(squats(40), pushUps(35), lunges(40), gluteBridge(35), plank(35), mountainClimbers(30)),
            totalDurationMin = 35, estimatedCalories = 230, intensity = WorkoutIntensity.HIGH, focusArea = "Full Body"
        )
    )

    private fun buildHiitSessions(): List<WorkoutSession> = listOf(
        WorkoutSession(
            id = "hiit_d1", dayNumber = 1, title = "Day 1 — Total Burn",
            exercises = listOf(sprintInPlace(20), jumpSquat(30), burpee(35), highKnees(40), mountainClimbers(30), sprintInPlace(20)),
            totalDurationMin = 20, estimatedCalories = 280, intensity = WorkoutIntensity.HIIT, focusArea = "Maximum Output"
        ),
        WorkoutSession(
            id = "hiit_d2", dayNumber = 2, title = "Day 2 — Power Circuit",
            exercises = listOf(jumpingJacks(30), jumpSquat(25), burpee(30), mountainClimbers(25), highKnees(30), sprintInPlace(20)),
            totalDurationMin = 20, estimatedCalories = 300, intensity = WorkoutIntensity.HIIT, focusArea = "Cardio Power"
        ),
        WorkoutSession(
            id = "hiit_d3", dayNumber = 3, title = "Day 3 — Explosive Training",
            exercises = listOf(burpee(35), jumpSquat(30), sprintInPlace(20), highKnees(40), mountainClimbers(30), jumpingJacks(35)),
            totalDurationMin = 20, estimatedCalories = 310, intensity = WorkoutIntensity.HIIT, focusArea = "Explosive Power"
        ),
        WorkoutSession(
            id = "hiit_d4", dayNumber = 4, title = "Day 4 — Finisher",
            exercises = listOf(sprintInPlace(20), burpee(35), jumpSquat(30), mountainClimbers(30), highKnees(40), sprintInPlace(20)),
            totalDurationMin = 20, estimatedCalories = 295, intensity = WorkoutIntensity.HIIT, focusArea = "Full Throttle"
        )
    )

    private fun buildBeginnerSessions(): List<WorkoutSession> = listOf(
        WorkoutSession(
            id = "beg_d1", dayNumber = 1, title = "Day 1 — Easy Start",
            exercises = listOf(jumpingJacks(30), squats(30), plank(20), lunges(30), gluteBridge(30)),
            totalDurationMin = 20, estimatedCalories = 130, intensity = WorkoutIntensity.EASY, focusArea = "Getting Started"
        ),
        WorkoutSession(
            id = "beg_d2", dayNumber = 2, title = "Day 2 — Build It Up",
            exercises = listOf(highKnees(30), pushUps(20), squats(30), bicycleCrunch(25), catCow(30)),
            totalDurationMin = 20, estimatedCalories = 140, intensity = WorkoutIntensity.EASY, focusArea = "Full Body Basics"
        ),
        WorkoutSession(
            id = "beg_d3", dayNumber = 3, title = "Day 3 — Core & More",
            exercises = listOf(jumpingJacks(35), plank(25), lunges(30), gluteBridge(30), childPose(35)),
            totalDurationMin = 20, estimatedCalories = 145, intensity = WorkoutIntensity.MODERATE, focusArea = "Core Foundation"
        )
    )

    private fun buildCycleSyncSessions(): List<WorkoutSession> = listOf(
        WorkoutSession(
            id = "cs_menstrual", dayNumber = 1, title = "Menstrual — Gentle Rest Flow",
            exercises = listOf(catCow(60), childPose(60), cobraPose(45), downwardDog(45), childPose(60)),
            totalDurationMin = 25, estimatedCalories = 60, intensity = WorkoutIntensity.EASY, focusArea = "Restorative Yoga"
        ),
        WorkoutSession(
            id = "cs_follicular", dayNumber = 2, title = "Follicular — Rising Energy",
            exercises = listOf(jumpingJacks(40), squats(40), pushUps(30), lunges(35), plank(30), highKnees(35)),
            totalDurationMin = 28, estimatedCalories = 220, intensity = WorkoutIntensity.MODERATE, focusArea = "Building Strength"
        ),
        WorkoutSession(
            id = "cs_ovulation", dayNumber = 3, title = "Ovulation — Peak Power",
            exercises = listOf(jumpSquat(30), burpee(35), highKnees(40), mountainClimbers(30), sprintInPlace(20), jumpingJacks(40)),
            totalDurationMin = 28, estimatedCalories = 300, intensity = WorkoutIntensity.HIIT, focusArea = "Peak Performance"
        ),
        WorkoutSession(
            id = "cs_luteal", dayNumber = 4, title = "Luteal — Calm & Core",
            exercises = listOf(squats(35), gluteBridge(40), plank(30), catCow(45), downwardDog(40), childPose(45)),
            totalDurationMin = 27, estimatedCalories = 160, intensity = WorkoutIntensity.MODERATE, focusArea = "Grounding"
        ),
        WorkoutSession(
            id = "cs_premenstrual", dayNumber = 5, title = "Pre-Menstrual — Wind Down",
            exercises = listOf(catCow(50), childPose(50), cobraPose(40), downwardDog(45), warriorI(35)),
            totalDurationMin = 25, estimatedCalories = 80, intensity = WorkoutIntensity.EASY, focusArea = "Gentle Restoration"
        )
    )

    // ─────────────────────────────────────────────
    // PROGRAM CATALOG
    // ─────────────────────────────────────────────

    val allPrograms: List<ExerciseProgram> = listOf(
        ExerciseProgram(
            id = "weight_loss", name = "Weight Loss Glow Up 🔥", emoji = "🔥",
            category = ProgramCategory.WEIGHT_LOSS,
            description = "Burn fat with high-energy cardio, HIIT, and full body circuits. 6 days a week, 30 mins each. Results in 4 weeks.",
            daysPerWeek = 6, sessionDurationMin = 30, difficulty = "Intermediate", totalWeeks = 4,
            weeklyPlan = buildWeightLossSessions(),
            highlights = listOf("🔥 300+ cal/session", "💪 6 days/week", "⚡ HIIT included", "📈 4-week plan")
        ),
        ExerciseProgram(
            id = "yoga", name = "Yoga for Women 🧘", emoji = "🧘",
            category = ProgramCategory.YOGA,
            description = "Flow through yoga designed for hormonal balance, flexibility and deep restoration. 5 days a week, 25 mins each.",
            daysPerWeek = 5, sessionDurationMin = 25, difficulty = "Beginner", totalWeeks = 4,
            weeklyPlan = buildYogaSessions(),
            highlights = listOf("🌸 Hormonal balance", "🧘 Stress relief", "💆 Deep restoration", "🌙 Better sleep")
        ),
        ExerciseProgram(
            id = "strength", name = "Strength Basics 💪", emoji = "💪",
            category = ProgramCategory.STRENGTH,
            description = "Build lean muscle and core strength with bodyweight exercises. 4 days a week, 35 mins each.",
            daysPerWeek = 4, sessionDurationMin = 35, difficulty = "Intermediate", totalWeeks = 4,
            weeklyPlan = buildStrengthSessions(),
            highlights = listOf("💪 Lean muscle", "🏋️ Bodyweight only", "🔥 Core focus", "📊 Progressive")
        ),
        ExerciseProgram(
            id = "hiit", name = "HIIT Boost ⚡", emoji = "⚡",
            category = ProgramCategory.HIIT,
            description = "Maximum calorie burn in minimum time. Intense 20-min HIIT sessions, 4 days a week. Not for beginners.",
            daysPerWeek = 4, sessionDurationMin = 20, difficulty = "Advanced", totalWeeks = 4,
            weeklyPlan = buildHiitSessions(),
            highlights = listOf("⚡ Max intensity", "⏱ Only 20 mins", "🔥 300+ cal", "🏆 Advanced")
        ),
        ExerciseProgram(
            id = "cycle_sync", name = "Cycle Sync 🌸", emoji = "🌸",
            category = ProgramCategory.CYCLE_SYNC,
            description = "Workouts that automatically sync with your menstrual cycle phases. Exercise smarter, not harder.",
            daysPerWeek = 5, sessionDurationMin = 28, difficulty = "All Levels", totalWeeks = 4,
            weeklyPlan = buildCycleSyncSessions(),
            highlights = listOf("🌸 Phase-matched", "🔄 Auto-adjusting", "✨ Feels natural", "💕 Women-first")
        ),
        ExerciseProgram(
            id = "beginner", name = "Beginner Glow Up ✨", emoji = "✨",
            category = ProgramCategory.BEGINNER,
            description = "Start your fitness journey with easy, confidence-building workouts. 3 days a week, 20 mins each. Perfect for beginners.",
            daysPerWeek = 3, sessionDurationMin = 20, difficulty = "Beginner", totalWeeks = 4,
            weeklyPlan = buildBeginnerSessions(),
            highlights = listOf("✨ Beginner friendly", "⏱ 20 mins only", "💕 Build confidence", "🌱 Start fresh")
        )
    )

    // ─────────────────────────────────────────────
    // STATE MANAGEMENT
    // ─────────────────────────────────────────────

    private val _joinedPrograms = MutableStateFlow<Map<String, ProgramProgress>>(
        mapOf("weight_loss" to ProgramProgress("weight_loss"))
    )
    val joinedPrograms: StateFlow<Map<String, ProgramProgress>> = _joinedPrograms.asStateFlow()

    private val _activeWorkout = MutableStateFlow<ActiveWorkoutState?>(null)
    val activeWorkout: StateFlow<ActiveWorkoutState?> = _activeWorkout.asStateFlow()

    fun joinProgram(programId: String) {
        val current = _joinedPrograms.value.toMutableMap()
        if (!current.containsKey(programId)) {
            current[programId] = ProgramProgress(programId)
            _joinedPrograms.value = current
        }
    }

    fun leaveProgram(programId: String) {
        val current = _joinedPrograms.value.toMutableMap()
        current.remove(programId)
        _joinedPrograms.value = current
    }

    fun isProgramJoined(programId: String): Boolean {
        return _joinedPrograms.value.containsKey(programId)
    }

    fun startSession(session: WorkoutSession) {
        _activeWorkout.value = ActiveWorkoutState(
            session = session,
            currentExerciseIndex = 0,
            secondsRemaining = session.exercises.firstOrNull()?.durationSeconds ?: 30
        )
    }

    fun markSessionComplete(programId: String, sessionId: String, caloriesBurned: Int) {
        val current = _joinedPrograms.value.toMutableMap()
        val progress = current[programId] ?: ProgramProgress(programId)
        current[programId] = progress.copy(
            completedSessionIds = progress.completedSessionIds + sessionId,
            streakDays = progress.streakDays + 1,
            totalCaloriesBurned = progress.totalCaloriesBurned + caloriesBurned
        )
        _joinedPrograms.value = current
        _activeWorkout.value = null
    }

    fun clearActiveWorkout() {
        _activeWorkout.value = null
    }

    fun getProgramById(id: String): ExerciseProgram? = allPrograms.find { it.id == id }

    fun getTodaySessionForProgram(programId: String, currentCyclePhase: String? = null): WorkoutSession? {
        val program = getProgramById(programId) ?: return null
        val progress = _joinedPrograms.value[programId]
        val completedCount = progress?.completedSessionIds?.size ?: 0
        val sessionIndex = completedCount % program.weeklyPlan.size

        // For Cycle Sync program, pick session based on cycle phase
        if (programId == "cycle_sync" && currentCyclePhase != null) {
            val phaseSession = when {
                currentCyclePhase.contains("Menstrual", ignoreCase = true) ->
                    program.weeklyPlan.find { it.id == "cs_menstrual" }
                currentCyclePhase.contains("Follicular", ignoreCase = true) ->
                    program.weeklyPlan.find { it.id == "cs_follicular" }
                currentCyclePhase.contains("Ovulation", ignoreCase = true) ->
                    program.weeklyPlan.find { it.id == "cs_ovulation" }
                else -> program.weeklyPlan.find { it.id == "cs_luteal" }
            }
            if (phaseSession != null) return phaseSession
        }

        return program.weeklyPlan.getOrNull(sessionIndex)
    }
}
