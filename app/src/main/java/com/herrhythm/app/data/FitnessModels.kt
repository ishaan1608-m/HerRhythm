package com.herrhythm.app.data

enum class ExerciseType {
    SQUAT, PUSH_UP, LUNGE, PLANK, JUMPING_JACK, HIGH_KNEES,
    MOUNTAIN_CLIMBER, BURPEE, BICYCLE_CRUNCH, RUSSIAN_TWIST,
    GLUTE_BRIDGE, DEAD_BUG, SIDE_PLANK, CALF_RAISE, TRICEP_DIP,
    CAT_COW, DOWNWARD_DOG, WARRIOR_I, CHILD_POSE, COBRA,
    TREE_POSE, SEATED_FORWARD_FOLD, PIGEON_POSE,
    JUMP_SQUAT, SPRINT_IN_PLACE, BOX_STEP, FLUTTER_KICK, LEG_RAISE
}

enum class MuscleGroup { FULL_BODY, CORE, LEGS, UPPER_BODY, GLUTES, BACK, SHOULDERS, CARDIO, FLEXIBILITY }

enum class WorkoutIntensity { EASY, MODERATE, HIGH, HIIT }

enum class ProgramCategory {
    WEIGHT_LOSS, YOGA, STRENGTH, HIIT, CYCLE_SYNC, BEGINNER
}

data class Exercise(
    val id: String,
    val name: String,
    val type: ExerciseType,
    val targetMuscles: List<MuscleGroup>,
    val durationSeconds: Int,           // 0 if rep-based
    val reps: Int = 0,                  // 0 if time-based
    val sets: Int = 1,
    val restAfterSeconds: Int = 15,
    val instructions: List<String>,     // Step-by-step cues
    val tips: String = "",
    val caloriesBurnedEstimate: Int = 5
)

data class WorkoutSession(
    val id: String,
    val dayNumber: Int,                 // Day within week
    val title: String,
    val exercises: List<Exercise>,
    val totalDurationMin: Int,
    val estimatedCalories: Int,
    val intensity: WorkoutIntensity,
    val focusArea: String
)

data class ExerciseProgram(
    val id: String,
    val name: String,
    val emoji: String,
    val category: ProgramCategory,
    val description: String,
    val daysPerWeek: Int,
    val sessionDurationMin: Int,
    val difficulty: String,
    val totalWeeks: Int = 4,
    val weeklyPlan: List<WorkoutSession>, // 1 week of sessions
    val highlights: List<String>,
    val isJoined: Boolean = false
)

data class ProgramProgress(
    val programId: String,
    val joinedDate: Long = System.currentTimeMillis(),
    val completedSessionIds: Set<String> = emptySet(),
    val currentWeek: Int = 1,
    val streakDays: Int = 0,
    val totalCaloriesBurned: Int = 0
)

data class ActiveWorkoutState(
    val session: WorkoutSession,
    val currentExerciseIndex: Int = 0,
    val isResting: Boolean = false,
    val secondsRemaining: Int = 0,
    val isCompleted: Boolean = false,
    val totalElapsedSeconds: Int = 0
)
