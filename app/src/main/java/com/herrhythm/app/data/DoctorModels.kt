package com.herrhythm.app.data

data class Doctor(
    val id: String,
    val name: String,
    val title: String,
    val specialty: String,
    val qualifications: String,
    val experienceYears: Int,
    val rating: Float,
    val reviewCount: Int,
    val hospitalClinicName: String,
    val clinicAddress: String,
    val distanceKm: Float,
    val onlineFee: Int,
    val inClinicFee: Int,
    val availableSlots: List<String>,
    val aboutBio: String,
    val languages: List<String>,
    val badgeTag: String = "Top Rated"
)

data class DoctorAppointment(
    val id: String = java.util.UUID.randomUUID().toString(),
    val doctorId: String,
    val doctorName: String,
    val doctorSpecialty: String,
    val doctorHospital: String,
    val date: String,
    val timeSlot: String,
    val isOnline: Boolean = true,
    val consultationType: String = "Online Video Call",
    val patientNotes: String = "",
    val status: String = "Confirmed", // Confirmed, Completed, Cancelled
    val feePaid: String = "₹600"
)
