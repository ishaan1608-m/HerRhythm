package com.herrhythm.app.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DoctorRepository(
    private val storageManager: LocalStorageManager? = null
) {
    val doctorsList: List<Doctor> = listOf(
        Doctor(
            id = "dr_ananya",
            name = "Dr. Ananya Sharma",
            title = "Senior Consultant Gynaecologist & Obstetrician",
            specialty = "PCOS, Hormonal Balance & Menstrual Health",
            qualifications = "MBBS, MS (OBGYN), DNB, Fellowship in Reproductive Medicine",
            experienceYears = 14,
            rating = 4.9f,
            reviewCount = 342,
            hospitalClinicName = "Apollo Cradle Women & Child Care",
            clinicAddress = "Sector 18, Block B, Near Metro Station",
            distanceKm = 2.4f,
            onlineFee = 600,
            inClinicFee = 850,
            availableSlots = listOf("10:00 AM", "11:30 AM", "02:00 PM", "04:30 PM", "06:00 PM", "07:30 PM"),
            aboutBio = "Specializes in PCOS management, hormonal imbalance, cycle irregularities, and holistic women's wellness. Known for compassionate and empathetic patient care.",
            languages = listOf("English", "Hindi"),
            badgeTag = "PCOS Specialist 🌸"
        ),
        Doctor(
            id = "dr_priyanka",
            name = "Dr. Priyanka Sengupta",
            title = "Lead Gynaecologist & Endometriosis Specialist",
            specialty = "Endometriosis, Severe Cramps & Pelvic Pain",
            qualifications = "MBBS, MD (OBGYN), MRCOG (London)",
            experienceYears = 18,
            rating = 4.95f,
            reviewCount = 512,
            hospitalClinicName = "Max Super Speciality Hospital",
            clinicAddress = "Wing 4, Healthcare Enclave, Central Ave",
            distanceKm = 4.1f,
            onlineFee = 700,
            inClinicFee = 1000,
            availableSlots = listOf("09:30 AM", "11:00 AM", "01:30 PM", "03:30 PM", "05:00 PM"),
            aboutBio = "Renowned specialist in chronic pelvic pain, endometriosis diagnosis, uterine fibroids, and advanced minimally invasive care.",
            languages = listOf("English", "Hindi", "Bengali"),
            badgeTag = "Pelvic Pain Expert 💕"
        ),
        Doctor(
            id = "dr_meera",
            name = "Dr. Meera Iyer",
            title = "Consultant Obstetrician & Fertility Specialist",
            specialty = "Pregnancy Care, Fertility & Preconception",
            qualifications = "MBBS, DGO, DNB, Masters in Clinical Embryology",
            experienceYears = 12,
            rating = 4.85f,
            reviewCount = 280,
            hospitalClinicName = "Cloudnine Hospital for Women",
            clinicAddress = "Plot 89, Ring Road, Next to Lotus Mall",
            distanceKm = 3.2f,
            onlineFee = 550,
            inClinicFee = 800,
            availableSlots = listOf("10:30 AM", "12:00 PM", "02:30 PM", "04:00 PM", "06:30 PM"),
            aboutBio = "Empathetic guide for pregnancy planning, prenatal monitoring, fertility counseling, and natural birth guidance.",
            languages = listOf("English", "Hindi", "Tamil"),
            badgeTag = "Fertility Guide ✨"
        ),
        Doctor(
            id = "dr_kavita",
            name = "Dr. Kavita Verma",
            title = "Adolescent Health & General Gynaecology",
            specialty = "Teen Period Health, Infections & Preventive Care",
            qualifications = "MBBS, MS - Obstetrics & Gynaecology",
            experienceYears = 9,
            rating = 4.8f,
            reviewCount = 195,
            hospitalClinicName = "Fortis La Femme Hospital",
            clinicAddress = "Gate 2, Green Park Avenue",
            distanceKm = 1.8f,
            onlineFee = 500,
            inClinicFee = 750,
            availableSlots = listOf("11:00 AM", "01:00 PM", "03:00 PM", "05:30 PM", "07:00 PM"),
            aboutBio = "Friendly, judgment-free consultations for young women, vaginal health, UTI prevention, contraception guidance, and menstrual hygiene.",
            languages = listOf("English", "Hindi"),
            badgeTag = "Friendly & Direct 🌷"
        )
    )

    private val _appointments = MutableStateFlow<List<DoctorAppointment>>(
        storageManager?.getAppointments()?.ifEmpty { getDefaultAppointments() } ?: getDefaultAppointments()
    )
    val appointments: StateFlow<List<DoctorAppointment>> = _appointments.asStateFlow()

    private fun getDefaultAppointments(): List<DoctorAppointment> {
        return listOf(
            DoctorAppointment(
                id = "demo_appt_1",
                doctorId = "dr_ananya",
                doctorName = "Dr. Ananya Sharma",
                doctorSpecialty = "PCOS & Menstrual Health",
                doctorHospital = "Apollo Cradle Women Care",
                date = "Tomorrow",
                timeSlot = "04:30 PM",
                isOnline = true,
                consultationType = "Online Video Call",
                patientNotes = "Routine cycle check-up & cramp guidance",
                status = "Confirmed",
                feePaid = "₹600"
            )
        )
    }

    fun bookAppointment(
        doctor: Doctor,
        date: String,
        timeSlot: String,
        isOnline: Boolean,
        patientNotes: String
    ): DoctorAppointment {
        val appt = DoctorAppointment(
            doctorId = doctor.id,
            doctorName = doctor.name,
            doctorSpecialty = doctor.specialty,
            doctorHospital = doctor.hospitalClinicName,
            date = date,
            timeSlot = timeSlot,
            isOnline = isOnline,
            consultationType = if (isOnline) "Online Video Call" else "In-Clinic Visit (${doctor.clinicAddress})",
            patientNotes = patientNotes,
            status = "Confirmed",
            feePaid = if (isOnline) "₹${doctor.onlineFee}" else "₹${doctor.inClinicFee}"
        )
        val updated = listOf(appt) + _appointments.value
        _appointments.value = updated
        storageManager?.saveAppointments(updated)
        return appt
    }

    fun cancelAppointment(appointmentId: String) {
        val updated = _appointments.value.map {
            if (it.id == appointmentId) it.copy(status = "Cancelled") else it
        }
        _appointments.value = updated
        storageManager?.saveAppointments(updated)
    }

    fun getDoctorById(id: String): Doctor? {
        return doctorsList.find { it.id == id }
    }
}
