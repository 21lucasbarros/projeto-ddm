package br.unisanta.projetoddm.data.repository

import br.unisanta.projetoddm.data.model.UserDoc
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {

    private val auth = FirebaseAuth.getInstance();
    private val users = FirebaseFirestore.getInstance().collection("users");

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null;
    }

    fun getCurrentUserId(): String {
        val user = auth.currentUser;
        if(user == null) return "";
        return user.uid;
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess }
            .addOnFailureListener { e -> onError(traduzirErro(e)) }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user == null) {
                    onError("Não foi possível criar a conta.")
                    return@addOnSuccessListener
                }
                users.document(user.uid).set(UserDoc(name, email))
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onError("Conta criada, mas falhou ao salvar o perfil: " + e.message) }
            }
            .addOnFailureListener { e -> onError(traduzirErro(e)) }
    }

    fun getUserName(onResult: (String) -> Unit) {
        val uid = getCurrentUserId()
        if (uid.isEmpty()) {
            onResult("Usuário")
            return
        }
        users.document(uid).get()
            .addOnSuccessListener { snapshot ->
                val userDoc = snapshot.toObject(UserDoc::class.java)
                if (userDoc != null && userDoc.name.isNotBlank()) {
                    onResult(userDoc.name)
                } else {
                    onResult("Usuário")
                }
            }
            .addOnFailureListener { onResult("Usuário") }
    }

    fun logout() {
        auth.signOut()
    }

    private fun traduzirErro(e: Exception): String {
        if (e is FirebaseAuthWeakPasswordException) return "Senha fraca, use pelo menos 6 caracteres."
        if (e is FirebaseAuthUserCollisionException) return "Já existe uma conta com esse e-mail."
        if (e is FirebaseAuthInvalidUserException) return "Usuário não encontrado."
        if (e is FirebaseAuthInvalidCredentialsException) return "E-mail ou senha inválidos."
        return "Erro: " + e.message
    }
 }