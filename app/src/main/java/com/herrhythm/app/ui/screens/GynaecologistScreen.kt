package com.herrhythm.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.herrhythm.app.data.Doctor
import com.herrhythm.app.data.DoctorAppointment
import com.herrhythm.app.data.DoctorRepository
import com.herrhythm.app.ui.theme.*

@Composable
fun GynaecologistScreen(
    doctorRepository: DoctorRepository,
    onBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedConsultationMode by remember { mutableStateOf("All") } // All, Online, In-Clinic
    var searchQuery by remember { mutableStateOf("") }
    var doctorToBook by remember { mutableStateOf<Doctor?>(null) }
    var activeTab by remember { mutableIntStateOf(0) } // 0 = Find Doctors, 1 = My Appointments

    val appointments by doctorRepository.appointments.collectAsState()

    val categories = listOf("All", "PCOS & Hormones", "Period & Cramps", "Pregnancy & Fertility", "General Gynae")

    val filteredDoctors = doctorRepository.doctorsList.filter { doc ->
        val matchesCategory = when (selectedCategory) {
            "PCOS & Hormones" -> doc.specialty.contains("PCOS", ignoreCase = true) || doc.badgeTag.contains("PCOS", ignoreCase = true)
            "Period & Cramps" -> doc.specialty.contains("Pain", ignoreCase = true) || doc.specialty.contains("Menstrual", ignoreCase = true) || doc.badgeTag.contains("Pelvic", ignoreCase = true)
            "Pregnancy & Fertility" -> doc.specialty.contains("Fertility", ignoreCase = true) || doc.specialty.contains("Pregnancy", ignoreCase = true)
            "General Gynae" -> doc.specialty.contains("General", ignoreCase = true) || doc.specialty.contains("Adolescent", ignoreCase = true)
            else -> true
        }

        val matchesSearch = doc.name.contains(searchQuery, ignoreCase = true) ||
                doc.specialty.contains(searchQuery, ignoreCase = true) ||
                doc.hospitalClinicName.contains(searchQuery, ignoreCase = true)

        matchesCategory && matchesSearch
    }

    if (doctorToBook != null) {
        BookAppointmentDialog(
            doctor = doctorToBook!!,
            onDismiss = { doctorToBook = null },
            onConfirmBooking = { date, time, isOnline, notes ->
                doctorRepository.bookAppointment(
                    doctor = doctorToBook!!,
                    date = date,
                    timeSlot = time,
                    isOnline = isOnline,
                    patientNotes = notes
                )
                doctorToBook = null
                activeTab = 1 // Switch to My Appointments
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBg)
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(CardSurface)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Gynaecologists & Specialists", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Verified women's health doctors 👩‍⚕️", fontSize = 12.sp, color = TextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // TAB SELECTOR: Find Doctors vs My Appointments
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(CardSurface)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (activeTab == 0) RosePrimary else Color.Transparent)
                    .clickable { activeTab = 0 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Find Doctors 🩺",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (activeTab == 0) Color.White else TextPrimary
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (activeTab == 1) RosePrimary else Color.Transparent)
                    .clickable { activeTab = 1 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "My Appointments",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (activeTab == 1) Color.White else TextPrimary
                    )
                    if (appointments.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (activeTab == 1) Color.White else RosePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${appointments.size}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeTab == 1) RosePrimary else Color.White
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (activeTab == 0) {
            // TAB 0: FIND DOCTORS
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search doctor, PCOS, fertility, clinic...", fontSize = 13.sp, color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = RosePrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RosePrimary,
                    unfocusedBorderColor = PeachBorder,
                    focusedContainerColor = CreamCard,
                    unfocusedContainerColor = CreamCard
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 6.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) RosePrimary else CardSurface)
                            .border(1.dp, if (isSelected) RosePrimary else PeachBorder, RoundedCornerShape(12.dp))
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Doctors List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredDoctors) { doctor ->
                    DoctorCard(
                        doctor = doctor,
                        onBook = { doctorToBook = doctor }
                    )
                }
            }
        } else {
            // TAB 1: MY APPOINTMENTS
            if (appointments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📅", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No Appointments Booked Yet", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Consult top gynecologists online or at clinic", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { activeTab = 0 },
                            colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Book Consultation Now →", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(appointments) { appt ->
                        AppointmentCard(
                            appointment = appt,
                            onCancel = { doctorRepository.cancelAppointment(appt.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DoctorCard(
    doctor: Doctor,
    onBook: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    // Doctor Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(SoftRose, CardSurface)))
                            .border(2.dp, RosePrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👩‍⚕️", fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(doctor.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(doctor.title, fontSize = 11.sp, color = TextMuted, maxLines = 1)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(doctor.qualifications, fontSize = 10.sp, color = TextSecondary, maxLines = 1)
                    }
                }

                // Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SoftRose)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(doctor.badgeTag, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RosePrimary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Rating, Experience & Distance Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(WarmSurface)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⭐ ${doctor.rating} (${doctor.reviewCount}+)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(WarmSurface)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("⏳ ${doctor.experienceYears} yrs exp", fontSize = 11.sp, color = TextPrimary)
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(WarmSurface)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("📍 ${doctor.distanceKm} km", fontSize = 11.sp, color = TextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "🏥 ${doctor.hospitalClinicName} — ${doctor.clinicAddress}",
                fontSize = 11.sp,
                color = TextSecondary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Price & Book Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Consultation Fee", fontSize = 10.sp, color = TextMuted)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("₹${doctor.onlineFee}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RosePrimary)
                        Text(" (Online)", fontSize = 11.sp, color = TextSecondary)
                        Text(" • ₹${doctor.inClinicFee}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(" (Clinic)", fontSize = 11.sp, color = TextSecondary)
                    }
                }

                Button(
                    onClick = onBook,
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text("Book Slot ✨", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun AppointmentCard(
    appointment: DoctorAppointment,
    onCancel: () -> Unit
) {
    var showJoinCallRoom by remember { mutableStateOf(false) }

    if (showJoinCallRoom) {
        VideoConsultationRoomDialog(
            appointment = appointment,
            onClose = { showJoinCallRoom = false }
        )
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👩‍⚕️", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(appointment.doctorName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(appointment.doctorSpecialty, fontSize = 11.sp, color = RosePrimary)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (appointment.status == "Confirmed") HealthGreen.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.3f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        appointment.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (appointment.status == "Confirmed") HealthGreen else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(WarmSurface)
                    .padding(10.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Event, contentDescription = null, tint = RosePrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${appointment.date} at ${appointment.timeSlot}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Type: ${appointment.consultationType} • Fee: ${appointment.feePaid}", fontSize = 11.sp, color = TextSecondary)
                    if (appointment.patientNotes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Reason: \"${appointment.patientNotes}\"", fontSize = 11.sp, color = TextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (appointment.status == "Confirmed") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (appointment.isOnline) {
                        Button(
                            onClick = { showJoinCallRoom = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Join Video Call 🎥", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    OutlinedButton(
                        onClick = onCancel,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) {
                        Text("Cancel", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun BookAppointmentDialog(
    doctor: Doctor,
    onDismiss: () -> Unit,
    onConfirmBooking: (String, String, Boolean, String) -> Unit
) {
    var selectedDate by remember { mutableStateOf("Tomorrow") }
    var selectedSlot by remember { mutableStateOf(doctor.availableSlots.firstOrNull() ?: "10:00 AM") }
    var isOnline by remember { mutableStateOf(true) }
    var notes by remember { mutableStateOf("") }

    val dateOptions = listOf("Today (Urgent)", "Tomorrow", "In 2 Days", "This Weekend")

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(CreamCard)
                .border(1.dp, PeachBorder, RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Book Consultation", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(doctor.name, fontSize = 13.sp, color = RosePrimary, fontWeight = FontWeight.SemiBold)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mode toggle (Online vs In-Clinic)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardSurface)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isOnline) RosePrimary else Color.Transparent)
                            .clickable { isOnline = true }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎥 Online Video (₹${doctor.onlineFee})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isOnline) Color.White else TextPrimary)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!isOnline) RosePrimary else Color.Transparent)
                            .clickable { isOnline = false }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏥 In-Clinic (₹${doctor.inClinicFee})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (!isOnline) Color.White else TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Date Selection
                Text("Select Date", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(dateOptions) { d ->
                        val isSelected = selectedDate == d
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SoftRose else CardSurface)
                                .border(1.dp, if (isSelected) RosePrimary else PeachBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedDate = d }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(d, fontSize = 11.sp, color = if (isSelected) RosePrimary else TextPrimary, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Slot Selection
                Text("Available Slots", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(doctor.availableSlots) { slot ->
                        val isSelected = selectedSlot == slot
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) RosePrimary else CardSurface)
                                .border(1.dp, if (isSelected) RosePrimary else PeachBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedSlot = slot }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(slot, fontSize = 11.sp, color = if (isSelected) Color.White else TextPrimary, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Patient Note / Reason
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Brief reason for visit (e.g. PCOS checkup, cramps, bleeding)", fontSize = 11.sp, color = TextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RosePrimary,
                        unfocusedBorderColor = PeachBorder,
                        focusedContainerColor = CreamBg,
                        unfocusedContainerColor = CreamBg
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onConfirmBooking(selectedDate, selectedSlot, isOnline, notes) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirm & Book Appointment ✨", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun VideoConsultationRoomDialog(
    appointment: DoctorAppointment,
    onClose: () -> Unit
) {
    var isMuted by remember { mutableStateOf(false) }
    var isVideoOff by remember { mutableStateOf(false) }
    var inCallChatText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1E1424))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top call header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(appointment.doctorName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Live Video Consultation • Connected 🔒", fontSize = 11.sp, color = HealthGreen)
                    }
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "End Call", tint = Color.White)
                    }
                }

                // Doctor Video Feed Simulation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF2C1D36))
                        .border(1.dp, RosePrimary, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👩‍⚕️", fontSize = 60.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(appointment.doctorName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(appointment.doctorSpecialty, fontSize = 12.sp, color = SoftRose)
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(HealthGreen.copy(alpha = 0.3f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("● Doctor is listening", fontSize = 10.sp, color = HealthGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Self Video Preview Overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .size(70.dp, 90.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF4A345B))
                            .border(1.dp, Color.White, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("You 🌸", fontSize = 10.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Call Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(if (isMuted) Color.Red else Color.DarkGray)
                    ) {
                        Icon(if (isMuted) Icons.Default.MicOff else Icons.Default.Mic, contentDescription = null, tint = Color.White)
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    ) {
                        Icon(Icons.Default.CallEnd, contentDescription = "End", tint = Color.White)
                    }

                    IconButton(
                        onClick = { isVideoOff = !isVideoOff },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(if (isVideoOff) Color.Red else Color.DarkGray)
                    ) {
                        Icon(if (isVideoOff) Icons.Default.VideocamOff else Icons.Default.Videocam, contentDescription = null, tint = Color.White)
                    }
                }
            }
        }
    }
}
