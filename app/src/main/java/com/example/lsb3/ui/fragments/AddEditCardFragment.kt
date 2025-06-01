package com.example.lsb3.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.lsb3.R
import com.example.lsb3.data.model.Card
import com.example.lsb3.databinding.FragmentAddEditCardBinding
import com.example.lsb3.ui.viewmodel.AddEditCardViewModel
import com.example.lsb3.usecase.CardValidator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel



class AddEditCardFragment : Fragment() {
    private lateinit var binding: FragmentAddEditCardBinding
    private val viewModel: AddEditCardViewModel by viewModel()
    private var cardId: Int = -1
    private var isEdit: Boolean = false
    private var selectedImageUri: Uri? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddEditCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                binding.iVSelectedImage.setImageURI(it)
            }
        }


        val activity = (requireActivity() as AppCompatActivity)
        activity.setSupportActionBar(binding.toolBar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        finish()
                        true
                    }
                    else -> false
                }
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        if (arguments?.containsKey("cardId")!=null) {
            isEdit = true
            binding.edit = true

            cardId = arguments?.getInt("cardId")!!

            CoroutineScope(Dispatchers.IO).launch{
                val card = viewModel.getCard(cardId)


                activity?.runOnUiThread {
                    Glide.with(binding.root.context)
                        .load(card?.shopImg)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.nophoto)
                        .into(binding.iVSelectedImage)

                    binding.card = card

                }
            }



        }
        else{
            binding.edit = false

            binding.iVSelectedImage.setOnClickListener {
                imagePickerLauncher.launch("image/*")  // открывает галерею
            }
        }

        binding.checkBox.setOnCheckedChangeListener{ checkBox, isChecked ->
            binding.ltDisc.isVisible = !isChecked
        }

        binding.btnBack.setOnClickListener{
            val name = binding.etName.text.toString()
            val shtr = binding.etShtr.text.toString()
            val disc = binding.etDisc.text.toString()
            val isDisc = !binding.checkBox.isChecked

            if (!CardValidator.validate(name,shtr,isDisc,disc)) {
                Toast.makeText(requireContext(), "Заполните все поля корректно", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedCard = Card(name, shtr, isDisc, if (isDisc) disc else null)

            CoroutineScope(Dispatchers.IO).launch{
                if (isEdit){
                    updatedCard.id = cardId
                    viewModel.editCard(updatedCard)
                }
                else{
                    viewModel.addCard(updatedCard)
                    if (selectedImageUri != null) {
                        viewModel.uploadImageToServer(requireContext(), selectedImageUri!!, name)
                        viewModel.cancelCoroutine()
                    }
                }

                withContext(Dispatchers.Main){
                    finish()
                }
            }
        }
    }

    private fun finish(){
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.frlayout, MainFragment())
            .commit()
    }
}

