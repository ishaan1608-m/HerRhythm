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
            .background(PookieDarkBg)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PookieCardBg)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Gynaecologists & Specialists", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Verified women's health doctors 👩‍⚕️", fontSize = 12.sp, color = PookieTextMuted)
            }
        }

        // TAB SELECTOR: Find Doctors vs My Appointments
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(PookieCardBg)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (activeTab == 0) PookiePinkPrimary else Color.Transparent)
                    .clickable { activeTab = 0 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Find Doctors 🩺",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (activeTab == 1) PookiePinkPrimary else Color.Transparent)
                    .clickable { activeTab = 1 }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "My Appointments",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (appointments.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (activeTab == 1) Color.White else PookiePinkPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${appointments.size}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (activeTab == 1) PookiePinkPrimary else Color.White
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
                placeholder = { Text("Search doctor, PCOS, fertility, clinic...", fontSize = 13.sp, color = PookieTextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PookiePinkPrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PookiePinkPrimary,
                    unfocusedBorderColor = PookieCardLight,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = PookieCardBg,
                    unfocusedContainerColor = PookieCardBg
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
                            .background(if (isSelected) PookiePinkPrimary else PookieCardBg)
                            .border(1.dp, if (isSelected) PookiePinkGlow else PookieCardLight, RoundedCornerShape(12.dp))
                            .clickable { selectedCategory = category }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = Color.White
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
                        Text("No Appointments Booked Yet", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Consult top gynecologists online or at clinic", fontSize = 12.sp, color = PookieTextMuted)
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { activeTab = 0 },
                            colors = ButtonDefaults.buttonColors(containerColor = PookiePinkPrimary),
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(PookieCardBg)
            .padding(16.dp)
    ) {
        Column {
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
                            .background(Brush.radialGradient(listOf(PookiePinkPrimary.copy(alpha = 0.3f), PookieCardLight)))
                            .border(2.dp, PookiePinkPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👩‍⚕️", fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(doctor.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(doctor.title, fontSize = 11.sp, color = PookieTextMuted, maxLines = 1)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(doctor.qualifications, fontSize = 10.sp, color = PookiePinkGlow, maxLines = 1)
                    }
                }

                // Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PookiePinkPrimary.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(doctor.badgeTag, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PookiePinkGlow)
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
                        .background(PookieCardLight)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⭐ ${doctor.rating} (${doctor.reviewCount}+)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PookieCardLight)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("⏳ ${doctor.experienceYears} yrs exp", fontSize = 11.sp, color = Color.White)
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PookieCardLight)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("📍 ${doctor.distanceKm} km", fontSize = 11.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "🏥 ${doctor.hospitalClinicName} — ${doctor.clinicAddress}",
                fontSize = 11.sp,
                color = PookieTextMuted,
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
                    Text("Consultation Fee", fontSize = 10.sp, color = PookieTextMuted)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("₹${doctor.onlineFee}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PookiePinkPrimary)
                        Text(" (Online)", fontSize = 11.sp, color = PookieTextMuted)
                        Text(" • ₹${doctor.inClinicFee}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(" (Clinic)", fontSize = 11.sp, color = PookieTextMuted)
                    }
                }

                Button(
                    onClick = onBook,
                    colors = ButtonDefaults.buttonColors(containerColor = PookiePinkPrimary),
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(PookieCardBg)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👩‍⚕️", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(appointment.doctorName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(appointment.doctorSpecialty, fontSize = 11.sp, color = PookiePinkGlow)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (appointment.status == "Confirmed") HealthGreen.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        appointment.status,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (appointment.status == "Confirmed") HealthGreen else PookieTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(PookieCardLight)
                    .padding(10.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Event, contentDescription = null, tint = PookiePinkPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${appointment.date} at ${appointment.timeSlot}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Type: ${appointment.consultationType} • Fee: ${appointment.feePaid}", fontSize = 11.sp, color = PookieTextMuted)
                    if (appointment.patientNotes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Reason: \"${appointment.patientNotes}\"", fontSize = 11.sp, color = PookiePinkGlow)
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
                            colors = ButtonDefaults.buttonColors(containerColor = PookiePinkPrimary),
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
                .background(PookieCardBg)
                .border(1.dp, PookieCardLight, RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Book Consultation", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(doctor.name, fontSize = 13.sp, color = PookiePinkPrimary, fontWeight = FontWeight.SemiBold)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mode toggle (Online vs In-Clinic)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(PookieCardLight)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isOnline) PookiePinkPrimary else Color.Transparent)
                            .clickable { isOnline = true }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎥 Online (₹${doctor.onlineFee})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!isOnline) PookiePinkPrimary else Color.Transparent)
                            .clickable { isOnline = false }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏥 Clinic (₹${doctor.inClinicFee})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Date Selection
                Text("Select Date", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(dateOptions) { d ->
                        val isSelected = selectedDate == d
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PookiePinkPrimary else PookieCardLight)
                                .clickable { selectedDate = d }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(d, fontSize = 11.sp, color = Color.White, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Slot Selection
                Text("Available Slots", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(doctor.availableSlots) { slot ->
                        val isSelected = selectedSlot == slot
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PookiePinkPrimary else PookieCardLight)
                                .clickable { selectedSlot = slot }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(slot, fontSize = 11.sp, color = Color.White, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Patient Note / Reason
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Brief reason for visit (e.g. PCOS, cramps, irregular cycle)", fontSize = 11.sp, color = PookieTextMuted) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = PookieCardLight,
                        unfocusedContainerColor = PookieCardLight
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onConfirmBooking(selectedDate, selectedSlot, isOnline, notes) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PookiePinkPrimary),
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

    Dialog(onDismissRequest = onClose) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF15132B))
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
                        .background(Color(0xFF221F3E))
                        .border(1.dp, PookiePinkPrimary, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("👩‍⚕️", fontSize = 60.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(appointment.doctorName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(appointment.doctorSpecialty, fontSize = 12.sp, color = PookiePinkGlow)
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(HealthGreen.copy(alpha = 0.3f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("● Doctor is connected & listening", fontSize = 10.sp, color = HealthGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Self Video Preview Overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .size(70.dp, 90.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF2C2850))
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
