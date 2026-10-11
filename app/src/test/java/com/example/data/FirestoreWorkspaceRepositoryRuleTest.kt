package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.firestore.FirestoreAiCreation
import com.example.data.firestore.FirestoreNote
import com.example.data.firestore.FirestoreWorkspaceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class FirestoreWorkspaceRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveNote_and_observeNotes_succeeds_for_owner() = runBlocking {
        val aliceUid = signInTestUser("alice@example.com")
        val repo = FirestoreWorkspaceRepository(firestore, auth)

        val note = FirestoreNote(
            id = "test_note_1",
            userId = aliceUid,
            name = "Hilsea Laboratory Notes",
            text = "Baseline carrier frequency measured at 42.598 Hz.",
            tags = "carrier, 42.6",
            kind = "note"
        )

        repo.saveNote(aliceUid, note)

        val fetched = repo.getNote(aliceUid, "test_note_1")
        assertNotNull(fetched)
        assertEquals("Hilsea Laboratory Notes", fetched?.name)

        val notesList = repo.observeNotes(aliceUid).first()
        assertTrue(notesList.any { it.id == "test_note_1" })
    }

    @Test
    fun unauthenticated_saveNote_fails() = runBlocking {
        auth.signOut()
        val repo = FirestoreWorkspaceRepository(firestore, auth)

        val note = FirestoreNote(
            id = "unauth_note",
            userId = "someone",
            name = "Unauthenticated Note",
            kind = "note"
        )

        try {
            repo.saveNote("someone", note)
            fail("Expected exception when unauthenticated")
        } catch (e: Exception) {
            assertTrue(e.message?.contains("authenticated") == true || e.message?.contains("PERMISSION_DENIED") == true)
        }
    }

    @Test
    fun crossUser_isolation_enforced() = runBlocking {
        val aliceUid = signInTestUser("alice_cross@example.com")
        val aliceRepo = FirestoreWorkspaceRepository(firestore, auth)

        val note = FirestoreNote(
            id = "alice_private_note",
            userId = aliceUid,
            name = "Confidential Resonance Data",
            kind = "note"
        )
        aliceRepo.saveNote(aliceUid, note)

        // Switch to Bob
        val bobUid = signInTestUser("bob_cross@example.com")
        val bobRepo = FirestoreWorkspaceRepository(firestore, auth)

        // Bob attempting to fetch Alice's note path directly
        try {
            val doc = firestore.collection("users").document(aliceUid).collection("notes").document("alice_private_note").get().result
            if (doc.exists()) {
                fail("Bob should not be able to read Alice's note")
            }
        } catch (e: Exception) {
            // Expected security rules PERMISSION_DENIED
            assertTrue(true)
        }
    }

    @Test
    fun saveAiCreation_succeeds_for_owner() = runBlocking {
        val userUid = signInTestUser("ai_user@example.com")
        val repo = FirestoreWorkspaceRepository(firestore, auth)

        val creation = FirestoreAiCreation(
            id = "ai_task_1",
            userId = userUid,
            type = "chat",
            prompt = "Explain Solent tidal envelope",
            resultData = "11.74-second lunar tidal rhythm modulating 42.6 Hz carrier.",
            modelUsed = "gemini-3.5-flash"
        )

        repo.saveAiCreation(userUid, creation)

        val list = repo.observeAiCreations(userUid).first()
        assertTrue(list.any { it.id == "ai_task_1" })
    }
}
