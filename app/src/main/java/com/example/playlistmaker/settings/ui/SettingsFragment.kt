package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.sharing.domain.models.IntentType
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            switchNightMode.isChecked = viewModel.isChecked()
            switchNightMode.setOnCheckedChangeListener { _, isChecked ->
                viewModel.switchTheme(isChecked)
            }

            shareApp.setOnClickListener {
                viewModel.makeIntent(IntentType.SEND_MESSAGE)
            }

            writeInSupport.setOnClickListener {
                viewModel.makeIntent(IntentType.SEND_EMAIL)

            }

            userAgreement.setOnClickListener {
                viewModel.makeIntent(IntentType.OPEN_BROWSER)

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}