// Doctor Portal JavaScript - Real-time Communication Engine

const chatChannel = new BroadcastChannel('herrhythm_chat_channel');

document.addEventListener('DOMContentLoaded', () => {
    const docLoginModal = document.getElementById('doc-login-modal');
    const btnLoginDoctor = document.getElementById('btn-login-doctor-portal');
    const portalMessages = document.getElementById('portal-chat-messages');
    const replyInput = document.getElementById('portal-reply-text');
    const btnSendReply = document.getElementById('btn-portal-send-reply');
    const toast = document.getElementById('toast');

    function showToast(msg) {
        if (!toast) return;
        toast.textContent = msg;
        toast.classList.add('show');
        setTimeout(() => toast.classList.remove('show'), 3000);
    }

    btnLoginDoctor?.addEventListener('click', () => {
        const name = document.getElementById('portal-doc-name')?.value || "Dr. Ananya Sharma";
        const license = document.getElementById('portal-doc-license')?.value || "MCI-2018-98421";
        
        document.getElementById('header-doc-name').textContent = `${name} Console`;
        document.getElementById('header-doc-license').textContent = `Verified License: ${license}`;
        docLoginModal?.classList.remove('active');
        showToast(`Doctor Authenticated & Logged In 🩺`);
    });

    function sendDoctorReply(text) {
        if (!text || !text.trim()) return;
        const cleanText = text.trim();

        // Append to Doctor Console view
        const docMsgDiv = document.createElement('div');
        docMsgDiv.className = 'chat-msg doc-msg';
        docMsgDiv.style.alignSelf = 'flex-end';
        docMsgDiv.innerHTML = `
            <div class="msg-bubble" style="background: linear-gradient(135deg, #FF4B82, #A048F8); color: white;">${cleanText}</div>
            <span class="msg-time" style="text-align:right;">Sent by You (Doctor) • Just now</span>
        `;
        portalMessages?.appendChild(docMsgDiv);
        portalMessages.scrollTop = portalMessages.scrollHeight;

        // Broadcast to Patient HerRhythm Web App
        chatChannel.postMessage({
            sender: 'doctor',
            docName: document.getElementById('portal-doc-name')?.value || 'Dr. Ananya Sharma',
            text: cleanText,
            timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
        });

        if (replyInput) replyInput.value = '';
    }

    btnSendReply?.addEventListener('click', () => {
        sendDoctorReply(replyInput?.value);
    });

    replyInput?.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') sendDoctorReply(replyInput.value);
    });

    // Receive incoming live messages from Patient HerRhythm App!
    chatChannel.onmessage = (event) => {
        const data = event.data;
        if (data && data.sender === 'patient') {
            showToast(`🔔 New Live Message from ${data.patientName || 'Patient'}!`);
            
            const patientMsgDiv = document.createElement('div');
            patientMsgDiv.className = 'chat-msg user-msg';
            patientMsgDiv.style.alignSelf = 'flex-start';
            patientMsgDiv.innerHTML = `
                <div class="msg-bubble" style="background: #1A102B; border: 1px solid #FF4B82; color: white;">
                    <strong style="color:#FF8FB3;">${data.patientName || 'Priya'}:</strong> ${data.text}
                </div>
                <span class="msg-time">Received • ${data.timestamp || 'Just now'}</span>
            `;
            portalMessages?.appendChild(patientMsgDiv);
            portalMessages.scrollTop = portalMessages.scrollHeight;
        }
    };

    document.getElementById('btn-doc-issue-rx')?.addEventListener('click', () => {
        showToast('Prescription Rx #8492 issued & sent to Patient Vault! 📄');
        sendDoctorReply('📄 Digital Prescription (Rx #8492) attached: Meftal-Spas 500mg (1-0-1) for cramp relief & Hydration plan.');
    });
});
