package carrillo.mariana.login

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties

data class Usuario(var name: String, var correo: String, var edad: Int, var fechanac: String)
