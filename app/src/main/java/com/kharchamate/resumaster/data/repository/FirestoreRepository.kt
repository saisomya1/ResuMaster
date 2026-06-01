package com.kharchamate.resumaster.data.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.firestore.PersistentCacheSettings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

/**
 * FirestoreRepository — central source of truth for all cloud data.
 *
 * Firestore structure:
 *   users/{uid}/
 *     selected_template  → { templateId, templateName, selectedAt }
 *     profile            → { fullName, email, phone }
 *
 * Offline persistence is enabled on initialization.
 */
class FirestoreRepository {

    companion object {
        private const val TAG = "FirestoreRepo"
        private const val COLLECTION_USERS = "users"
        private const val DOC_SELECTED_TEMPLATE = "selected_template"
        private const val DOC_PROFILE = "profile"
    }

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore

    init {
        // Enable offline persistence (Firestore SDK v25+ uses new API)
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
            .build()
        db = FirebaseFirestore.getInstance()
        db.firestoreSettings = settings
        Log.d(TAG, "Firestore initialized with offline persistence enabled")
    }

    // ─── Auth Helpers ────────────────────────────────────────────────────────

    /** Returns current user UID, or null if not authenticated */
    val currentUid: String?
        get() = auth.currentUser?.uid

    val isAuthenticated: Boolean
        get() = auth.currentUser != null

    val userDisplayName: String
        get() = auth.currentUser?.displayName?.takeIf { it.isNotBlank() }
            ?: auth.currentUser?.email?.substringBefore("@")
            ?: "User"

    // ─── Selected Template ────────────────────────────────────────────────────

    /**
     * Saves the selected template to:
     * users/{uid}/selected_template
     */
    fun saveSelectedTemplate(templateId: String, templateName: String, onResult: (Boolean, String?) -> Unit) {
        val uid = currentUid
        if (uid == null) {
            Log.w(TAG, "saveSelectedTemplate: user not authenticated")
            onResult(false, "User not authenticated")
            return
        }

        val data = hashMapOf(
            "templateId" to templateId,
            "templateName" to templateName,
            "selectedAt" to Timestamp.now()
        )

        db.collection(COLLECTION_USERS)
            .document(uid)
            .collection("settings")
            .document(DOC_SELECTED_TEMPLATE)
            .set(data)
            .addOnSuccessListener {
                Log.d(TAG, "Template saved: $templateId for uid=$uid")
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to save template: ${e.message}", e)
                onResult(false, e.message)
            }
    }

    /**
     * Real-time listener for selected template.
     * Emits the templateId string whenever Firestore changes.
     */
    fun getSelectedTemplateFlow(): Flow<String?> = callbackFlow {
        val uid = currentUid
        if (uid == null) {
            Log.w(TAG, "getSelectedTemplateFlow: user not authenticated — emitting null")
            trySend(null)
            close()
            return@callbackFlow
        }

        val docRef = db.collection(COLLECTION_USERS)
            .document(uid)
            .collection("settings")
            .document(DOC_SELECTED_TEMPLATE)

        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Template snapshot error: ${error.message}", error)
                trySend(null)
                return@addSnapshotListener
            }
            val templateId = snapshot?.getString("templateId")
            Log.d(TAG, "Template snapshot received: $templateId (fromCache=${snapshot?.metadata?.isFromCache})")
            trySend(templateId)
        }

        awaitClose {
            Log.d(TAG, "getSelectedTemplateFlow: listener removed")
            listener.remove()
        }
    }

    // ─── User Profile ─────────────────────────────────────────────────────────

    /**
     * Saves basic profile to users/{uid}/profile
     */
    fun saveProfile(fullName: String, email: String, phone: String, onResult: (Boolean, String?) -> Unit) {
        val uid = currentUid ?: run {
            onResult(false, "Not authenticated"); return
        }

        val data = hashMapOf(
            "fullName" to fullName,
            "email" to email,
            "phone" to phone,
            "updatedAt" to Timestamp.now()
        )

        db.collection(COLLECTION_USERS)
            .document(uid)
            .collection("data")
            .document(DOC_PROFILE)
            .set(data)
            .addOnSuccessListener {
                Log.d(TAG, "Profile saved for uid=$uid")
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to save profile: ${e.message}", e)
                onResult(false, e.message)
            }
    }

    // ─── Diagnostic / Debug ───────────────────────────────────────────────────

    /**
     * Test read — reads the users/{uid} document and returns diagnostic info.
     */
    fun testRead(onResult: (success: Boolean, message: String) -> Unit) {
        val uid = currentUid
        if (uid == null) {
            onResult(false, "Not authenticated — UID is null")
            return
        }

        db.collection(COLLECTION_USERS)
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val fromCache = doc.metadata.isFromCache
                val msg = "READ OK — uid=$uid | exists=${doc.exists()} | fromCache=$fromCache"
                Log.d(TAG, msg)
                onResult(true, msg)
            }
            .addOnFailureListener { e ->
                val msg = "READ FAILED — ${e.message}"
                Log.e(TAG, msg, e)
                onResult(false, msg)
            }
    }

    /**
     * Test write — writes a diagnostic ping to users/{uid}/debug/ping
     */
    fun testWrite(onResult: (success: Boolean, message: String) -> Unit) {
        val uid = currentUid
        if (uid == null) {
            onResult(false, "Not authenticated — UID is null")
            return
        }

        val data = hashMapOf(
            "ping" to "pong",
            "timestamp" to Timestamp.now()
        )

        db.collection(COLLECTION_USERS)
            .document(uid)
            .collection("debug")
            .document("ping")
            .set(data)
            .addOnSuccessListener {
                val msg = "WRITE OK — uid=$uid"
                Log.d(TAG, msg)
                onResult(true, msg)
            }
            .addOnFailureListener { e ->
                val msg = "WRITE FAILED — ${e.message}"
                Log.e(TAG, msg, e)
                onResult(false, msg)
            }
    }
}
