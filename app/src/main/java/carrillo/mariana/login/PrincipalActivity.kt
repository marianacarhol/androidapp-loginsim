package carrillo.mariana.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carrillo.mariana.login.ui.theme.LoginTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class PrincipalActivity : ComponentActivity() {

    private lateinit var myRef: DatabaseReference
    private val reloadKey = mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val uid = Firebase.auth.currentUser?.uid ?: ""
        myRef = Firebase.database.getReference("usuarios").child(uid)

        setContent {
            LoginTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaInicio(
                        myRef,
                        reloadKey = reloadKey.value,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        reloadKey.value++
    }
}

@Composable
fun PantallaInicio(myRef: DatabaseReference, reloadKey: Int, modifier: Modifier = Modifier) {

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var fechanac by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(reloadKey) {
        myRef.get().addOnSuccessListener { snapshot ->
            nombre = snapshot.child("name").value.toString()
            correo = snapshot.child("correo").value.toString()
            edad = snapshot.child("edad").value.toString()
            fechanac = snapshot.child("fechanac").value.toString()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "BIENVENIDO", fontSize = 40.sp)
        Spacer(modifier = modifier.height(16.dp))
        Text(text = "$nombre", fontSize = 32.sp)
        Spacer(modifier = modifier.height(16.dp))
        Text(text = "$correo", fontSize = 32.sp)
        Spacer(modifier = modifier.height(16.dp))
        Text(text = "$fechanac", fontSize = 32.sp)
        Spacer(modifier = modifier.height(16.dp))
        Text(text = "$edad", fontSize = 32.sp)
        Spacer(modifier = modifier.height(16.dp))
        Button(onClick = {
            val intent = Intent(context, ActualizarDatosActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(text = "Actualizar Datos")
        }
        Button(onClick = {
            Firebase.auth.signOut()
            (context as? Activity)?.finish()
        }) {
            Text(text = "Cerrar Sesión")
        }
    }
}
