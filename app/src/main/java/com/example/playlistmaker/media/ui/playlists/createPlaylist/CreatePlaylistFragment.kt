package com.example.playlistmaker.media.ui.playlists.createPlaylist

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.bundle.bundleOf
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.playlistmaker.R
import com.example.playlistmaker.common.utils.Formatter
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePlaylistFragment() : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<CreatePlaylistViewModel>()

    private var uriImage = Uri.EMPTY

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCreate.isEnabled = false

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.isNotEmpty() == true)
                    binding.buttonCreate.isEnabled = true
                else if (p0?.isNullOrEmpty() == true)
                    binding.buttonCreate.isEnabled = false
            }

            override fun afterTextChanged(p0: Editable?) {}

        }

        binding.editTextName.addTextChangedListener(textWatcher)

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Glide.with(this)
                        .load(uri).apply(
                            RequestOptions().transform(
                                MultiTransformation(
                                    CenterCrop(), RoundedCorners(
                                        Formatter.dpToPx(8f, view.context)
                                    )
                                )
                            )
                        )
                        .into(binding.image)
                    uriImage = uri
                } else {
                    Toast.makeText(requireContext(), "Изображение не выбрано", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        binding.toolbar.setOnClickListener {
            if (
                !(binding.editTextName.text.isNullOrEmpty())
                || !(binding.editTextDescription.text.isNullOrEmpty())
                || uriImage != Uri.EMPTY
            ) showDialog()
            else
                closeFragment()
        }

        binding.image.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.buttonCreate.setOnClickListener {
            viewModel.createPlaylist(
                binding.editTextName.text.toString(),
                binding.editTextDescription.text.toString(),
                uriImage,
                binding.image.drawable.toBitmap()
            )
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist) + " ${binding.editTextName.text.toString()} "
                        + getString(R.string.created),
                Toast.LENGTH_SHORT
            ).show()
            closeFragment()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (
                    !(binding.editTextName.text.isNullOrEmpty())
                    || !(binding.editTextDescription.text.isNullOrEmpty())
                    || uriImage != Uri.EMPTY
                ) {
                    showDialog()
                } else {
                    closeFragment()
                }
            }
        })
    }

    fun showDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.finish_creating))
            .setMessage(getString(R.string.all_data_will_be_deleted)) // Описание диалога
            .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                dialog.cancel()
            }
            .setPositiveButton(getString(R.string.finish)) { dialog, which -> // Добавляет кнопку «Да»
                closeFragment()
            }
            .create()

        dialog.show()

        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL)

        val color = ContextCompat.getColor(requireContext(), R.color.blue)

        positiveButton.setTextColor(color)
        neutralButton.setTextColor(color)
    }

    private fun closeFragment() {
        setFragmentResult("fragment_key", bundleOf("closed" to true))
        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}