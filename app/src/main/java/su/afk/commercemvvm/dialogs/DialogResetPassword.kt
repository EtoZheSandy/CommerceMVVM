package su.afk.commercemvvm.dialogs

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import su.afk.commercemvvm.R

// функция расширения для fragment что бы вызывать диалог сброса пароля
fun Fragment.setupBottomDialog(
    onSendClick: (String) -> Unit // передать функция для отправки
) {
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle) // стиль для диалога
    val view = layoutInflater.inflate(R.layout.dialog_reset_password, null)

    dialog.setContentView(view = view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED // что бы диалог был над клавиатурой полностью а не частично
    dialog.show()

    val edEmail = view.findViewById<EditText>(R.id.edResetPassword)
    val buttonCancel = view.findViewById<Button>(R.id.buttonCancel)
    val buttonSendResetPassword = view.findViewById<Button>(R.id.buttonSendResetPassword)

    buttonSendResetPassword.setOnClickListener {
        val email = edEmail.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }

    buttonCancel.setOnClickListener {
        dialog.dismiss()
    }
}
