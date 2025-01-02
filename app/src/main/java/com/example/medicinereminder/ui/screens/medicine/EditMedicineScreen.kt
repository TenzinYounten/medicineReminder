package com.example.medicinereminder.ui.screens.medicine

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.medicinereminder.data.entity.Medicine
import com.example.medicinereminder.ui.components.common.ImagePicker
import com.example.medicinereminder.util.ImageStorage
import com.example.medicinereminder.viewmodel.MedicineViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMedicineScreen(
    medicineId: Long,
    medicineViewModel: MedicineViewModel,
    onNavigateBack: () -> Unit
) {
    val medicine by medicineViewModel.selectedMedicine.collectAsState()
    val context = LocalContext.current
    val imageStorage = remember { ImageStorage(context) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var originalImagePath by remember { mutableStateOf<String?>(null) }

    // Load medicine data when it becomes available
    LaunchedEffect(medicineId) {
        medicineViewModel.loadMedicineById(medicineId)
    }

    // Update local state when medicine data changes
    LaunchedEffect(medicine) {
        medicine?.let {
            name = it.name
            dosage = it.dosage
            instructions = it.instructions ?: ""
            originalImagePath = it.imageUri
            if (it.imageUri != null) {
                selectedImageUri = imageStorage.getImageUri(it.imageUri)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Medicine") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            ImagePicker(
                imageUri = selectedImageUri,
                onImagePicked = { uri ->
                    selectedImageUri = uri
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Medicine Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                label = { Text("Dosage") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it },
                label = { Text("Instructions") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        medicine?.let { currentMedicine ->
                            // Handle image update
                            val newImagePath = when {
                                selectedImageUri == null -> null // Image removed
                                selectedImageUri == originalImagePath?.let { imageStorage.getImageUri(it) } ->
                                    originalImagePath // Image unchanged
                                else -> selectedImageUri?.let { imageStorage.saveImage(it) } // New image
                            }

                            // Delete old image if it's being replaced or removed
                            if (originalImagePath != null && originalImagePath != newImagePath) {
                                imageStorage.deleteImage(originalImagePath!!)
                            }

                            val updatedMedicine = currentMedicine.copy(
                                name = name,
                                imageUri = newImagePath,
                                dosage = dosage,
                                instructions = if (instructions.isBlank()) null else instructions,
                                updatedAt = System.currentTimeMillis()
                            )
                            medicineViewModel.updateMedicine(updatedMedicine)
                            onNavigateBack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && dosage.isNotBlank()
            ) {
                Text("Save Changes")
            }
        }
    }
}