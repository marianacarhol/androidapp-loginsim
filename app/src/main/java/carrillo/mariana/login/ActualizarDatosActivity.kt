package carrillo.mariana.login

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carrillo.mariana.login.ui.theme.LoginTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import java.util.Calendar

class ActualizarDatosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val uid = Firebase.auth.currentUser?.uid ?: ""
        val myRef = Firebase.database.getReference("usuarios").child(uid)

        setContent {
            LoginTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaActualizar(
                        myRef,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaActualizar(myRef: DatabaseReference, modifier: Modifier = Modifier) {

    var nombre by remember { mutableStateOf("") }
    var fechanac by remember { mutableStateOf("") }

    var errorNombre by remember { mutableStateOf(false) }
    var errorFechanac by remember { mutableStateOf(false) }
    var errorEdad by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        myRef.get().addOnSuccessListener { snapshot ->
            nombre = snapshot.child("name").value.toString()
            fechanac = snapshot.child("fechanac").value.toString()
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Editar datos", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it; errorNombre = false },
            label = { Text("Nombre Completo") },
            isError = errorNombre,
            supportingText = { if (errorNombre) Text("Campo obligatorio") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = fechanac,
            onValueChange = { fechanac = it; errorFechanac = false; errorEdad = false },
            label = { Text("Fecha de Nacimiento") },
            placeholder = { Text("dd/mm/aaaa") },
            isError = errorFechanac || errorEdad,
            supportingText = {
                when {
                    errorFechanac -> Text("Ingresa una fecha válida (dd/mm/aaaa)")
                    errorEdad -> Text("Debes ser mayor de 18 años")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                errorNombre = nombre.isBlank()

                var edadCalculada = 0
                var esMayorEdad = false

                if (fechanac.isNotBlank()) {
                    try {
                        val partes = fechanac.split("/")
                        if (partes.size != 3) throw Exception()
                        val dia = partes[0].toInt()
                        val mes = partes[1].toInt() - 1
                        val anio = partes[2].toInt()
                        val hoy = Calendar.getInstance()
                        val nacimiento = Calendar.getInstance()
                        nacimiento.set(anio, mes, dia)
                        edadCalculada = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
                        if (hoy.get(Calendar.MONTH) < nacimiento.get(Calendar.MONTH) ||
                            (hoy.get(Calendar.MONTH) == nacimiento.get(Calendar.MONTH) &&
                                    hoy.get(Calendar.DAY_OF_MONTH) < nacimiento.get(Calendar.DAY_OF_MONTH))) {
                            edadCalculada--
                        }
                        if (edadCalculada >= 18) esMayorEdad = true else errorEdad = true
                    } catch (e: Exception) {
                        errorFechanac = true
                    }
                } else {
                    errorFechanac = true
                }

                if (!errorNombre && !errorFechanac && !errorEdad && esMayorEdad) {
                    myRef.child("name").setValue(nombre)
                    myRef.child("fechanac").setValue(fechanac)
                    myRef.child("edad").setValue(edadCalculada)
                    Toast.makeText(context, "Datos actualizados", Toast.LENGTH_SHORT).show()
                    (context as? Activity)?.finish()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar cambios")
        }

        TextButton(
            onClick = { (context as? Activity)?.finish() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}