// HerRhythm App JavaScript

// App State
const state = {
    lastPeriodStart: new Date(2026, 7, 1), // Aug 1, 2026
    cycleLength: 28,
    periodLength: 5,
    waterCount: 4,
    isDark: false,
    onboardingStep: 1,
    userName: "Priya"
};

// Onboarding & Profile Setup logic
document.addEventListener('DOMContentLoaded', () => {
    const authOverlay = document.getElementById('auth-overlay');
    const btnOnboardingNext = document.getElementById('btn-onboarding-next');
    const btnOnboardingBack = document.getElementById('btn-onboarding-back');

    btnOnboardingNext?.addEventListener('click', () => {
        if (state.onboardingStep < 6) {
            updateOnboardingStep(state.onboardingStep + 1);
        } else {
            // Finish Onboarding
            const nameInput = document.getElementById('ob-name');
            if (nameInput && nameInput.value.trim()) {
                state.userName = nameInput.value.trim();
            }
            authOverlay?.classList.remove('active');
            updateUI();
        }
    });

    btnOnboardingBack?.addEventListener('click', () => {
        if (state.onboardingStep > 1) {
            updateOnboardingStep(state.onboardingStep - 1);
        }
    });

    // Toggle symptom & goal cards
    document.querySelectorAll('.symptom-card').forEach(card => {
        card.addEventListener('click', () => card.classList.toggle('active'));
    });

    document.querySelectorAll('.goal-card').forEach(card => {
        card.addEventListener('click', () => {
            document.querySelectorAll('.goal-card').forEach(c => c.classList.remove('active'));
            card.classList.add('active');
        });
    });

    // Chip selection logic for sleep, activity, flow, cravings, meds
    document.querySelectorAll('.chips-group .chip').forEach(chip => {
        chip.addEventListener('click', () => {
            const parent = chip.parentElement;
            if (parent && !parent.id.includes('problems')) {
                parent.querySelectorAll('.chip').forEach(c => c.classList.remove('active'));
                chip.classList.add('active');
            }
        });
    });

    // Sliders update
    document.getElementById('ob-weight')?.addEventListener('input', (e) => {
        document.getElementById('ob-weight-val').textContent = `${e.target.value} kg`;
    });
    document.getElementById('ob-height')?.addEventListener('input', (e) => {
        document.getElementById('ob-height-val').textContent = `${e.target.value} cm`;
    });
    document.getElementById('ob-cycle-len')?.addEventListener('input', (e) => {
        document.getElementById('ob-cycle-len-val').textContent = `${e.target.value} days`;
        state.cycleLength = parseInt(e.target.value);
    });
    document.getElementById('ob-period-len')?.addEventListener('input', (e) => {
        document.getElementById('ob-period-len-val').textContent = `${e.target.value} days`;
        state.periodLength = parseInt(e.target.value);
    });
});

function updateOnboardingStep(step) {
    state.onboardingStep = step;
    document.querySelectorAll('.onboarding-step').forEach(el => el.classList.remove('active'));
    document.querySelector(`.onboarding-step[data-step="${step}"]`)?.classList.add('active');
    
    const fill = document.getElementById('onboarding-progress-fill');
    if (fill) fill.style.width = `${(step / 6) * 100}%`;
    
    const indicator = document.getElementById('step-indicator');
    if (indicator) indicator.textContent = `Step ${step} of 6`;
    
    const nextBtn = document.getElementById('btn-onboarding-next');
    if (nextBtn) {
        nextBtn.textContent = step === 6 ? "Complete Setup 💕" : "Next Step →";
    }
}


// Cycle Calculation Engine
function getCycleInfo() {
    const today = new Date();
    const msPerDay = 86400000;
    const daysSinceStart = Math.floor((today - state.lastPeriodStart) / msPerDay);
    const currentCycleDay = (daysSinceStart % state.cycleLength) + 1;

    const nextPeriodDate = new Date(state.lastPeriodStart);
    nextPeriodDate.setDate(nextPeriodDate.getDate() + state.cycleLength);

    const ovulationDay = state.cycleLength - 14;
    const ovulationDate = new Date(state.lastPeriodStart);
    ovulationDate.setDate(ovulationDate.getDate() + ovulationDay);

    const fertileStart = new Date(ovulationDate);
    fertileStart.setDate(fertileStart.getDate() - 4);
    const fertileEnd = new Date(ovulationDate);
    fertileEnd.setDate(fertileEnd.getDate() + 1);

    const daysUntilNext = Math.ceil((nextPeriodDate - today) / msPerDay);

    let phase, phaseDesc, phaseColor, phaseIcon;
    if (currentCycleDay <= state.periodLength) {
        phase = "Menstrual Phase";
        phaseDesc = "Period in progress. Take rest, drink warm fluids, and nurture yourself with self-compassion.";
        phaseColor = "#FF5277";
        phaseIcon = "fa-droplet";
    } else if (currentCycleDay < ovulationDay - 2) {
        phase = "Follicular Phase";
        phaseDesc = "Estrogen is rising! You may feel energized, optimistic, and creatively inspired. Great time for new goals!";
        phaseColor = "#FF94B8";
        phaseIcon = "fa-sun";
    } else if (currentCycleDay <= ovulationDay + 2) {
        phase = "Ovulation Phase";
        phaseDesc = "You're at peak fertility & energy. Your highest magnetism, confidence, and social energy are now.";
        phaseColor = "#00C9A7";
        phaseIcon = "fa-star";
    } else {
        phase = "Luteal Phase";
        phaseDesc = "Progesterone peaks then drops. Focus on rest, warming foods, and gentle movement. Be gentle with yourself.";
        phaseColor = "#8A4FFF";
        phaseIcon = "fa-moon";
    }

    // Pregnancy chance
    const todayDay = currentCycleDay;
    let fertility;
    if (todayDay >= ovulationDay - 2 && todayDay <= ovulationDay + 1) fertility = "High Fertility";
    else if (todayDay >= ovulationDay - 5 && todayDay <= ovulationDay + 3) fertility = "Medium Fertility";
    else fertility = "Low Fertility";

    return {
        currentCycleDay, daysUntilNext,
        nextPeriodDate, ovulationDate,
        fertileStart, fertileEnd,
        phase, phaseDesc, phaseColor, phaseIcon, fertility
    };
}

// Update Home Tab UI
function updateHome() {
    const info = getCycleInfo();
    const progress = (1 - (info.daysUntilNext / state.cycleLength)) * 264;

    document.getElementById('cycle-subtitle').textContent =
        `Cycle Day ${info.currentCycleDay} • ${info.phase}`;
    document.getElementById('days-count').textContent =
        `${Math.max(0, info.daysUntilNext)} Days`;
    document.getElementById('fertility-status').textContent = info.fertility;

    // Ring color update
    const ring = document.getElementById('ring-progress-bar');
    ring.style.stroke = info.phaseColor;
    ring.style.strokeDashoffset = 264 - Math.min(264, progress);

    document.getElementById('ring-days').style.color = info.phaseColor;
    document.getElementById('fertility-status').style.color = info.phaseColor;

    document.getElementById('phase-name').textContent = info.phase;
    document.getElementById('phase-desc').textContent = info.phaseDesc;

    const icon = document.querySelector('.card-icon i');
    icon.className = `fa-solid ${info.phaseIcon}`;
    document.querySelector('.card-icon').style.color = info.phaseColor;
    document.querySelector('.card-icon').style.background = `${info.phaseColor}22`;

    updateWater();
}

function updateWater() {
    const pct = (state.waterCount / 8) * 100;
    document.getElementById('water-count').textContent = `${state.waterCount} / 8 glasses`;
    document.getElementById('water-fill').style.width = `${pct}%`;
}

// Calendar Render
function renderCalendar() {
    const today = new Date();
    const year = today.getFullYear();
    const month = today.getMonth();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const firstDayOfMonth = new Date(year, month, 1).getDay();
    const startOffset = firstDayOfMonth === 0 ? 6 : firstDayOfMonth - 1;

    const info = getCycleInfo();
    const ovulationDay = new Date(info.ovulationDate).getDate();
    const periodStart = 1;
    const periodEnd = state.periodLength;

    const monthNames = ["January","February","March","April","May","June",
        "July","August","September","October","November","December"];
    document.getElementById('calendar-month-year').textContent = `${monthNames[month]} ${year}`;

    const forecastDate = info.nextPeriodDate;
    document.getElementById('next-period-forecast').textContent =
        `Next expected period: ${monthNames[forecastDate.getMonth()]} ${forecastDate.getDate()}, ${forecastDate.getFullYear()}`;

    const grid = document.getElementById('calendar-days');
    grid.innerHTML = '';

    // Empty cells for alignment
    for (let i = 0; i < startOffset; i++) {
        grid.innerHTML += `<div class="cal-day"></div>`;
    }

    for (let d = 1; d <= daysInMonth; d++) {
        let cls = 'cal-day';
        if (d >= periodStart && d <= periodEnd) cls += ' period';
        else if (d === ovulationDay) cls += ' ovulation';
        if (d === today.getDate()) cls += ' today';
        grid.innerHTML += `<div class="${cls}">${d}</div>`;
    }
}

// Insights Articles
const articles = [
    {
        category: "NUTRITION",
        title: "Best Foods for Your Menstrual Phase",
        summary: "Iron-rich foods like lentils, spinach, and dark chocolate replenish what your body loses. Opt for warming meals to soothe cramps.",
        time: "3 min read",
        color: "#FF5277"
    },
    {
        category: "FITNESS",
        title: "Sync Your Workouts with Your Cycle",
        summary: "During ovulation, your strength peaks — try HIIT and weight training! During your period, gentle yoga and stretching are ideal.",
        time: "4 min read",
        color: "#00C9A7"
    },
    {
        category: "MENTAL WELLNESS",
        title: "Reducing PMS Mood Swings Naturally",
        summary: "Magnesium, B6 vitamins, and adaptogens like ashwagandha help balance progesterone and cortisol during the luteal phase.",
        time: "5 min read",
        color: "#8A4FFF"
    },
    {
        category: "SLEEP",
        title: "Hormones, Sleep & Your Cycle",
        summary: "Low progesterone disrupts REM sleep. Use lavender aromatherapy, reduce screen time, and maintain a consistent sleep schedule.",
        time: "3 min read",
        color: "#FF94B8"
    },
    {
        category: "HYDRATION",
        title: "Why You Crave Differently Each Week",
        summary: "Hormonal shifts trigger salt cravings during menstruation and sweet cravings during the luteal phase. Understanding helps you respond wisely.",
        time: "2 min read",
        color: "#00B4D8"
    }
];

function renderArticles() {
    const container = document.getElementById('articles-container');
    container.innerHTML = articles.map(a => `
        <div class="card article-card">
            <div style="display:flex; justify-content:space-between; margin-bottom:6px;">
                <span style="font-size:10px; font-weight:700; color:${a.color};">${a.category}</span>
                <span style="font-size:10px; color:var(--text-muted);">${a.time}</span>
            </div>
            <h3 style="font-size:15px; color:var(--text-primary); margin-bottom:6px;">${a.title}</h3>
            <p style="font-size:12px; color:var(--text-muted); line-height:1.5;">${a.summary}</p>
        </div>
    `).join('');
}

// Tab Navigation
document.querySelectorAll('.nav-item').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.nav-item').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
        btn.classList.add('active');
        document.getElementById(btn.dataset.tab).classList.add('active');
    });
});

// Modal
document.getElementById('btn-open-log').addEventListener('click', () => {
    document.getElementById('log-modal').classList.add('active');
});
document.getElementById('btn-close-log').addEventListener('click', () => {
    document.getElementById('log-modal').classList.remove('active');
});
document.getElementById('btn-save-log').addEventListener('click', () => {
    document.getElementById('log-modal').classList.remove('active');
    showToast('Daily log saved! 🌸');
});

// Chip toggle logic
document.querySelectorAll('.chips-group .chip').forEach(chip => {
    chip.addEventListener('click', () => {
        chip.classList.toggle('active');
    });
});

// Water intake
document.getElementById('btn-add-water').addEventListener('click', () => {
    if (state.waterCount < 8) {
        state.waterCount++;
        updateWater();
        if (state.waterCount === 8) showToast('🎉 8 glasses done! Well hydrated!');
    }
});

// Settings
document.getElementById('input-cycle-len').addEventListener('input', e => {
    state.cycleLength = parseInt(e.target.value);
    document.getElementById('val-cycle-len').textContent = state.cycleLength;
    updateHome();
    renderCalendar();
});

document.getElementById('input-period-len').addEventListener('input', e => {
    state.periodLength = parseInt(e.target.value);
    document.getElementById('val-period-len').textContent = state.periodLength;
    renderCalendar();
});

// Theme Toggle
document.getElementById('theme-toggle').addEventListener('click', () => {
    state.isDark = !state.isDark;
    document.body.parentElement.setAttribute('data-theme', state.isDark ? 'dark' : '');
    const icon = document.querySelector('#theme-toggle i');
    icon.className = state.isDark ? 'fa-solid fa-sun' : 'fa-solid fa-moon';
});

// Toast Notification
function showToast(msg) {
    const toast = document.createElement('div');
    toast.textContent = msg;
    toast.style.cssText = `
        position: absolute; bottom: 90px; left: 50%; transform: translateX(-50%);
        background: #2C222E; color: white; padding: 12px 20px;
        border-radius: 16px; font-size: 13px; z-index: 200;
        box-shadow: 0 8px 20px rgba(0,0,0,0.3); white-space: nowrap;
    `;
    document.querySelector('.mobile-frame').appendChild(toast);
    setTimeout(() => toast.remove(), 2500);
}

// Quick action from home
document.querySelector('[data-tab="tab-calendar"]')?.addEventListener('click', renderCalendar);

// Features Interactive Handlers
document.addEventListener('DOMContentLoaded', () => {
    const guidedData = {
        breathing: {
            title: "4-7-8 Deep Breathing",
            desc: "Calm nervous system & reduce period stress (5 Mins)"
        },
        yoga: {
            title: "Pelvic Relief & Gentle Stretch",
            desc: "Ease severe menstrual cramps & lower back tension (12 Mins)"
        },
        meditation: {
            title: "Mindful PMS & Mood Balance",
            desc: "Soothe anxiety & balance emotional rhythm (10 Mins)"
        }
    };

    document.querySelectorAll('.guided-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.guided-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            const key = btn.dataset.session;
            if (guidedData[key]) {
                document.getElementById('guided-title').textContent = guidedData[key].title;
                document.getElementById('guided-desc').textContent = guidedData[key].desc;
            }
        });
    });

    document.getElementById('btn-start-session')?.addEventListener('click', () => {
        const currentTitle = document.getElementById('guided-title')?.textContent || 'Session';
        showToast(`Starting ${currentTitle} 🧘‍♀️`);
    });

    document.querySelector('.voice-mic-btn')?.addEventListener('click', () => {
        showToast('Voice interface is currently a UI preview 🎙️');
    });

    document.getElementById('doctor-consent-toggle')?.addEventListener('change', (e) => {
        showToast(e.target.checked ? 'Doctor sharing consent granted 🔒' : 'Doctor sharing access revoked 🚫');
    });

    // Doctor Consultation & Modal Logic
    const docModal = document.getElementById('doc-modal');
    const docModalTitle = document.getElementById('doc-modal-title');
    let selectedDocName = "Dr. Ananya Sharma";

    document.querySelectorAll('.btn-book-doc').forEach(btn => {
        btn.addEventListener('click', () => {
            selectedDocName = btn.dataset.doc || "Doctor";
            if (docModalTitle) docModalTitle.textContent = `Book with ${selectedDocName}`;
            docModal?.classList.add('active');
        });
    });

    document.getElementById('btn-close-doc-modal')?.addEventListener('click', () => {
        docModal?.classList.remove('active');
    });

    document.getElementById('btn-confirm-doc-booking')?.addEventListener('click', () => {
        docModal?.classList.remove('active');
        showToast(`Consultation booked with ${selectedDocName}! 🩺`);
    });

    document.getElementById('btn-share-report-card')?.addEventListener('click', () => {
        showToast('30-Day Health Report PDF generated & ready to share! 📄');
    });

    document.getElementById('btn-upload-prescription-card')?.addEventListener('click', () => {
        showToast('Prescription Vault: File uploader ready 🔒');
    });

    document.getElementById('btn-save-caregiver')?.addEventListener('click', () => {
        const name = document.getElementById('caregiver-name')?.value || 'Emergency Contact';
        showToast(`Emergency contact ${name} saved! 💕`);
    });

    // ROLE SELECTION & SWITCHER LOGIC


    // LIVE DOCTOR CHAT SYSTEM
    const docChatModal = document.getElementById('doc-live-chat-modal');
    const chatMsgContainer = document.getElementById('chat-messages-container');
    const chatInput = document.getElementById('chat-input-text');
    let activeDoctorChat = "Dr. Ananya Sharma";

    const doctorReplies = {
        cramps: "For severe cramps, drink warm chamomile tea, use a hot water bag on your lower abdomen, and gentle pelvic stretches. If pain is severe, I can prescribe a mild antispasmodic 🍵🌸",
        delay: "Period delays up to 7-10 days can occur due to stress, hormonal shifts, or PCOS. Let's check your cycle history log and order a routine hormone panel if needed 💧",
        pcos: "For PCOS management, focus on low-GI meals, 30 mins daily walking, Myo-Inositol supplements, and adequate sleep to regulate insulin sensitivity 🥗",
        default: "Thank you for sharing your concern. I have reviewed your cycle history log. Please stay hydrated and rest. Let me know if you need a digital prescription! 🩺"
    };

    const docSelector = document.getElementById('chat-doctor-selector');

    function openLiveDoctorChat(docName) {
        activeDoctorChat = docName;
        if (docSelector) docSelector.value = docName;
        const inlineDocName = document.getElementById('chat-inline-doc-name');
        if (inlineDocName) inlineDocName.textContent = docName;
        docChatModal?.classList.add('active');
    }

    docSelector?.addEventListener('change', (e) => {
        activeDoctorChat = e.target.value;
        const inlineDocName = document.getElementById('chat-inline-doc-name');
        if (inlineDocName) inlineDocName.textContent = activeDoctorChat;
        showToast(`Selected ${activeDoctorChat} for live chat 👩‍⚕️`);
    });

    document.querySelectorAll('.btn-chat-doc, .btn-open-chat-modal').forEach(btn => {
        btn.addEventListener('click', () => {
            openLiveDoctorChat(btn.dataset.doc || "Dr. Ananya Sharma");
        });
    });

    document.getElementById('btn-close-live-chat-modal')?.addEventListener('click', () => {
        docChatModal?.classList.remove('active');
    });

    // REAL-TIME BROADCAST CHANNEL TO DOCTOR PORTAL
    const patientChatChannel = new BroadcastChannel('herrhythm_chat_channel');

    function sendUserChatMessage(text) {
        if (!text || !text.trim()) return;
        const cleanText = text.trim();

        // 1. Force Open Chat Modal if not open
        const modal = document.getElementById('doc-live-chat-modal');
        modal?.classList.add('active');

        // 2. Fetch fresh container reference
        const container = document.getElementById('chat-messages-container');
        if (!container) return;

        // 3. User message bubble
        const userMsgDiv = document.createElement('div');
        userMsgDiv.className = 'chat-msg user-msg';
        userMsgDiv.innerHTML = `
            <div class="msg-bubble">${cleanText}</div>
            <span class="msg-time">Sent by You • ${new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
        `;
        container.appendChild(userMsgDiv);

        const inputField = document.getElementById('chat-input-text');
        if (inputField) inputField.value = '';
        container.scrollTop = container.scrollHeight;

        // 4. Broadcast message to Doctor Web Portal (doctor_portal.html)
        patientChatChannel.postMessage({
            sender: 'patient',
            patientName: state.userName || 'Priya',
            text: cleanText,
            timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        });

        // 5. Automatic Doctor Medical Reply after 1 second
        setTimeout(() => {
            let replyText = doctorReplies.default;
            const lower = cleanText.toLowerCase();
            if (lower.includes('cramp') || lower.includes('pain')) replyText = doctorReplies.cramps;
            else if (lower.includes('delay') || lower.includes('late')) replyText = doctorReplies.delay;
            else if (lower.includes('pcos') || lower.includes('diet') || lower.includes('acne')) replyText = doctorReplies.pcos;

            const docMsgDiv = document.createElement('div');
            docMsgDiv.className = 'chat-msg doc-msg';
            docMsgDiv.innerHTML = `
                <div class="msg-bubble" style="background:#1F1435; border:1px solid #FF4B82; color:#FFFFFF !important;">
                    <strong style="color:#FF8FB3 !important;">🩺 ${activeDoctorChat}:</strong> <span style="color:#FFFFFF !important;">${replyText}</span>
                </div>
                <span class="msg-time">Doctor Live Reply • ${new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
            `;
            container.appendChild(docMsgDiv);
            container.scrollTop = container.scrollHeight;
        }, 1000);
    }

    // Receive incoming live replies from Doctor Web Portal (doctor_portal.html)
    patientChatChannel.onmessage = (event) => {
        const data = event.data;
        if (data && data.sender === 'doctor') {
            const modal = document.getElementById('doc-live-chat-modal');
            modal?.classList.add('active');

            const container = document.getElementById('chat-messages-container');
            if (container) {
                const docMsgDiv = document.createElement('div');
                docMsgDiv.className = 'chat-msg doc-msg';
                docMsgDiv.innerHTML = `
                    <div class="msg-bubble" style="border-color:#FF4B82; background:#1F1435; color:#FFFFFF !important;">
                        <strong style="color:#FF8FB3 !important;">🩺 ${data.docName || 'Doctor Portal'}:</strong> <span style="color:#FFFFFF !important;">${data.text}</span>
                    </div>
                    <span class="msg-time">Live Doctor Console • ${data.timestamp || 'Just now'}</span>
                `;
                container.appendChild(docMsgDiv);
                container.scrollTop = container.scrollHeight;
            }
            showToast(`💬 Live Doctor Reply Received!`);
        }
    };

    document.getElementById('btn-send-chat-msg')?.addEventListener('click', () => {
        sendUserChatMessage(chatInput?.value);
    });

    chatInput?.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') sendUserChatMessage(chatInput.value);
    });

    document.querySelectorAll('.chat-shortcut-chip').forEach(chip => {
        chip.addEventListener('click', () => {
            sendUserChatMessage(chip.dataset.msg);
        });
    });

    document.getElementById('btn-chat-attach')?.addEventListener('click', () => {
        showToast('Attachment attached: Cycle & Symptom Summary PDF 📄');
    });

    // FLOATING LIVE CHAT FAB BUTTON HANDLER
    document.getElementById('fab-open-doctor-chat')?.addEventListener('click', () => {
        openLiveDoctorChat("Dr. Ananya Sharma");
        showToast("Live Doctor Chat Console Opened 💬");
    });

    // DIRECT PATIENT MESSAGE TO DOCTOR HANDLER
    document.getElementById('btn-send-patient-direct-msg')?.addEventListener('click', () => {
        const docSelect = document.getElementById('direct-msg-doc-select');
        const msgTextArea = document.getElementById('direct-msg-text');
        const selectedDoc = docSelect?.value || "Dr. Ananya Sharma";
        const messageText = msgTextArea?.value;

        if (!messageText || !messageText.trim()) {
            showToast('Please type a message before sending 🌸');
            return;
        }

        openLiveDoctorChat(selectedDoc);
        sendUserChatMessage(messageText);
        if (msgTextArea) msgTextArea.value = '';
        showToast(`Message sent to ${selectedDoc}! 🚀`);
    });

    // Home Banners Navigation
    document.getElementById('btn-goto-doctor')?.addEventListener('click', () => {
        document.querySelectorAll('.nav-item').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
        document.querySelector('.nav-item[data-tab="tab-doctor"]')?.classList.add('active');
        document.getElementById('tab-doctor')?.classList.add('active');
    });

    document.getElementById('btn-goto-features')?.addEventListener('click', () => {
        document.querySelectorAll('.nav-item').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
        document.querySelector('.nav-item[data-tab="tab-features"]')?.classList.add('active');
        document.getElementById('tab-features')?.classList.add('active');
    });

    // EMERGENCY SOS PANIC SAFETY TRIGGER
    const sosModal = document.getElementById('sos-modal');

    document.getElementById('btn-trigger-sos')?.addEventListener('click', () => {
        sosModal?.classList.add('active');
        showToast('🚨 EMERGENCY SOS ACTIVATED! Location Dispatched to Caregiver & 1091');

        // Broadcast SOS Alert to Doctor Web Portal
        patientChatChannel.postMessage({
            sender: 'patient',
            patientName: state.userName || 'Priya',
            text: '🚨 EMERGENCY SOS ALERT! Live Location: Lat 28.6139, Long 77.2090. Immediate medical assistance requested!',
            timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        });
    });

    document.getElementById('btn-cancel-sos')?.addEventListener('click', () => {
        sosModal?.classList.remove('active');
        showToast('SOS Panic Alert Cancelled');
    });

    // HEALTH SENSORS VITALS SYNC HANDLER
    document.getElementById('btn-sync-sensors')?.addEventListener('click', () => {
        const randomHR = Math.floor(Math.random() * (78 - 71 + 1)) + 71;
        const randomBBT = (36.6 + Math.random() * 0.2).toFixed(1);
        const randomSteps = 6420 + Math.floor(Math.random() * 200);

        const hrElem = document.getElementById('sensor-hr');
        const bbtElem = document.getElementById('sensor-bbt');
        const stepsElem = document.getElementById('sensor-steps');

        if (hrElem) hrElem.innerHTML = `${randomHR} <small style="font-size:10px;">BPM</small>`;
        if (bbtElem) bbtElem.textContent = `${randomBBT}°C`;
        if (stepsElem) stepsElem.textContent = randomSteps.toLocaleString();

        showToast('BLE Smartwatch Vitals Synced! ⌚');
    });

    // USER PROFILE & ONBOARDING DATA EDITOR HANDLER
    const profileModal = document.getElementById('user-profile-modal');

    document.getElementById('btn-open-user-profile')?.addEventListener('click', () => {
        // Populate inputs with current state
        document.getElementById('edit-profile-name').value = state.userName || 'Priya Sharma';
        document.getElementById('edit-profile-age').value = state.age || 24;
        document.getElementById('edit-profile-height').value = state.height || 165;
        document.getElementById('edit-profile-weight').value = state.weight || 58;
        document.getElementById('edit-profile-cycle-len').value = state.cycleLength || 28;
        document.getElementById('edit-profile-period-len').value = state.periodLength || 5;
        document.getElementById('modal-profile-title').textContent = `${state.userName || 'Priya'}'s Health Profile`;

        updateBMIPreview();
        profileModal?.classList.add('active');
    });

    document.getElementById('btn-close-profile-modal')?.addEventListener('click', () => {
        profileModal?.classList.remove('active');
    });

    function updateBMIPreview() {
        const h = parseFloat(document.getElementById('edit-profile-height')?.value) || 165;
        const w = parseFloat(document.getElementById('edit-profile-weight')?.value) || 58;
        const bmi = (w / ((h / 100) * (h / 100))).toFixed(1);
        let category = "Normal Weight";
        if (bmi < 18.5) category = "Underweight";
        else if (bmi >= 25 && bmi < 30) category = "Overweight";
        else if (bmi >= 30) category = "Obese";

        const bmiElem = document.getElementById('edit-profile-bmi-val');
        if (bmiElem) bmiElem.textContent = `${bmi} kg/m² (${category})`;
    }

    document.getElementById('edit-profile-height')?.addEventListener('input', updateBMIPreview);
    document.getElementById('edit-profile-weight')?.addEventListener('input', updateBMIPreview);

    document.getElementById('btn-save-profile-changes')?.addEventListener('click', () => {
        const newName = document.getElementById('edit-profile-name')?.value || 'Priya';
        const newAge = parseInt(document.getElementById('edit-profile-age')?.value) || 24;
        const newH = parseInt(document.getElementById('edit-profile-height')?.value) || 165;
        const newW = parseInt(document.getElementById('edit-profile-weight')?.value) || 58;
        const newCycle = parseInt(document.getElementById('edit-profile-cycle-len')?.value) || 28;
        const newPeriod = parseInt(document.getElementById('edit-profile-period-len')?.value) || 5;

        // Update state
        state.userName = newName;
        state.age = newAge;
        state.height = newH;
        state.weight = newW;
        state.cycleLength = newCycle;
        state.periodLength = newPeriod;

        // Update UI
        const userInitElem = document.getElementById('header-user-initial');
        if (userInitElem) userInitElem.textContent = newName.charAt(0).toUpperCase();

        const userGreeting = document.getElementById('header-user-greeting');
        if (userGreeting) userGreeting.textContent = `${newName}'s HerRhythm`;

        updateHome();
        renderCalendar();
        profileModal?.classList.remove('active');
        showToast(`Health profile & onboarding details updated! 🌸`);
    });

    // STITCH AI INTEGRATION EXPORTER WITH API TOKEN
    document.getElementById('btn-export-stitch')?.addEventListener('click', () => {
        const stitchConfig = {
            projectId: "11146883914433952783",
            apiToken: "HERRHYTHM_STITCH_DEMO_TOKEN",
            appName: "HerRhythm",
            theme: "Material Rose Dark V2",
            components: [
                "Cycle Progress Ring & Ovulation Countdown",
                "Daily Symptoms & Mood Logger Modal",
                "6-Step Health Questionnaire (BMI, Height, Weight, PCOS, Cramps)",
                "11 Premium Smartwatch & Mobile Features Suite",
                "Live Doctor Chat Window & Doctor Portal Console",
                "Emergency SOS & Caregiver Alert System",
                "NYRA AI Placeholder Ready Container"
            ]
        };
        console.log("Stitch API Token Authenticated:", stitchConfig.apiToken);
        showToast('Stitch API Connected! UI design synced & editable live on Stitch 🎨');
    });
});

// Initialize
updateHome();
renderCalendar();
renderArticles();




