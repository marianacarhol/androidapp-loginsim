package carrillo.mariana.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import carrillo.mariana.login.ui.theme.LoginTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import java.util.Calendar

class RegistroActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth;

    private lateinit var database: DatabaseReference;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = Firebase.auth

        database = Firebase.database.reference
        setContent {
            LoginTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaRegistro(
                        auth,
                        database,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaRegistro(auth:FirebaseAuth, database: DatabaseReference, modifier: Modifier = Modifier) {

    var nombre by remember {
        mutableStateOf("")
    }

    var correo by remember {
        mutableStateOf("")
    }

    var contra by remember {
        mutableStateOf("")
    }

    var contra2 by remember {
        mutableStateOf("")
    }

    var fechanac by remember {
        mutableStateOf("")
    }

    var errorNombre by remember {
        mutableStateOf(false)
    }

    var errorCorreo by remember {
        mutableStateOf(false)
    }

    var errorContra by remember {
        mutableStateOf(false)
    }

    var errorContra2 by remember {
        mutableStateOf(false)
    }

    var errorFechanac by remember {
        mutableStateOf(false)
    }

    var errorEdad by remember {
        mutableStateOf(false)
    }

    var errorContraIguales by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text =  "Registro", fontSize =  24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it
                errorNombre = false
            },
            label = { Text(text = "Nombre Completo")},
            isError = errorNombre,
            supportingText = {
                if (errorNombre) Text("Campo obligatorio")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it
                errorCorreo = false },
            label = { Text(text = "Correo Electrónico")},
            isError = errorCorreo,
            supportingText = {
                if (errorCorreo) Text("Ingresa un correo válido")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = contra,
            onValueChange = { contra = it
                errorContra = false},
            label = { Text(text = "Contraseña")},
            isError = errorContra,
            supportingText = {
                if (errorContra) {
                    if (errorContraIguales) Text("Las contraseñas no coinciden")
                    else Text("Campo obligatorio")
                }
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = contra2,
            onValueChange = { contra2 = it
                errorContra2 = false},
            label = { Text(text = "Verificar Contraseña")},
            isError = errorContra2,
            supportingText = {
                if (errorContra2) {
                    if (errorContraIguales) Text("Las contraseñas no coinciden")
                    else Text("Campo obligatorio")
                }
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = fechanac,
            onValueChange = { fechanac = it
                errorFechanac = false
                errorEdad = false },
            label = { Text(text = "Fecha de Nacimiento")},
            isError = errorFechanac || errorEdad,
            supportingText = {
                when {
                    errorFechanac -> Text("Ingresa una fecha válida (dd/mm/aaaa)")
                    errorEdad -> Text("Debes ser mayor de 18 años")
                }
            },
            placeholder = { Text(text = "dd/mm/aaaa")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {

                errorNombre = nombre.isBlank()
                errorCorreo = correo.isBlank()

                val emailRegex = Patterns.EMAIL_ADDRESS
                errorCorreo = correo.isBlank() || !emailRegex.matcher(correo).matches()


                val contrasenaVaciaA = contra.isBlank()
                val contrasenaVaciaB = contra2.isBlank()
                errorContra = contrasenaVaciaA
                errorContra2 = contrasenaVaciaB

                if (!contrasenaVaciaA && !contrasenaVaciaB && contra != contra2) {
                    errorContra = true
                    errorContra2 = true
                    errorContraIguales = true
                }

                var esMayorEdad = false
                var edad = 0

                if (fechanac.isNotBlank()) {
                    try {
                        val partes = fechanac.split("/")
                        if (partes.size != 3) throw Exception("Formato inválido")

                        val dia = partes[0].toInt()
                        val mes = partes[1].toInt() - 1
                        val anio = partes[2].toInt()

                        val hoy = Calendar.getInstance()
                        val nacimiento = Calendar.getInstance()
                        nacimiento.set(anio, mes, dia)

                        edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
                        if (hoy.get(Calendar.MONTH) < nacimiento.get(Calendar.MONTH) || (hoy.get(Calendar.MONTH) == nacimiento.get(Calendar.MONTH) && hoy.get(Calendar.DAY_OF_MONTH) < nacimiento.get(Calendar.DAY_OF_MONTH))) {
                            edad--
                        }

                        if (edad >= 18) {
                            esMayorEdad = true
                        } else {
                            errorEdad = true
                        }

                    } catch (e: Exception) {
                        errorFechanac = true
                    }
                } else {
                    errorFechanac = true
                }

                if (!errorNombre && !errorCorreo && !errorContra && !errorContra2 && !errorFechanac && !errorEdad && esMayorEdad) {
                    auth.createUserWithEmailAndPassword(correo, contra)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userID = auth.currentUser?.uid ?: "anonimo"
                                val usuario = Usuario(nombre, correo, edad, fechanac)
                                database.child("usuarios").child(userID).setValue(usuario)
                                Toast.makeText(context, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show()
                                val intent = Intent(context, PrincipalActivity::class.java)
                                intent.putExtra("correo", correo)
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "No se pudo crear la cuenta, intenta de nuevo", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }

        TextButton(
            onClick = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}