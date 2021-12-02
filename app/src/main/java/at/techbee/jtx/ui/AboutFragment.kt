/*
 * Copyright (c) Techbee e.U.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package at.techbee.jtx.ui

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import at.techbee.jtx.BuildConfig
import at.techbee.jtx.MainActivity
import at.techbee.jtx.R
import at.techbee.jtx.databinding.FragmentAboutBinding
import at.techbee.jtx.databinding.FragmentAboutJtxBinding
import at.techbee.jtx.databinding.FragmentAboutThanksBinding
import com.google.android.material.tabs.TabLayout
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import java.text.SimpleDateFormat

class AboutFragment : Fragment() {

    lateinit var binding: FragmentAboutBinding
    lateinit var application: Application
    private lateinit var inflater: LayoutInflater

    companion object {

        private const val TAB_POSITION_ABOUT = 0
        private const val TAB_POSITION_LIBRARIES = 1
        private const val TAB_POSITION_TRANSLATIONS = 2
        private const val TAB_POSITION_THANKS = 3
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Get a reference to the binding object and inflate the fragment views.
        this.inflater = inflater
        this.binding = FragmentAboutBinding.inflate(inflater, container, false)
        this.application = requireNotNull(this.activity).application

        val bindingAboutJtx = FragmentAboutJtxBinding.inflate(inflater, container, false)
        val bindingAboutThanks = FragmentAboutThanksBinding.inflate(inflater, container, false)


        //val aboutBinding = FragmentAboutBinding.inflate(inflater, container, false)
        bindingAboutJtx.aboutAppVersion.text = getString(R.string.about_app_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
        bindingAboutJtx.aboutAppCodename.text = getString(R.string.about_app_codename, BuildConfig.versionCodename)
        bindingAboutJtx.aboutAppBuildTime.text = getString(R.string.about_app_build_date, SimpleDateFormat.getDateInstance().format(BuildConfig.buildTime))
        binding.aboutLinearlayout.removeAllViews()
        binding.aboutLinearlayout.addView(bindingAboutJtx.root)

        // get the fragment for the about library
        val aboutLibrariesFragment = LibsBuilder()
            .withFields(R.string::class.java.fields)        // mandatory for non-standard build flavors
            .withLicenseShown(true)
            .withAboutIconShown(false)
            // https://github.com/mikepenz/AboutLibraries/issues/490
            .withLibraryModification("org_brotli__dec", Libs.LibraryFields.LIBRARY_NAME, "Brotli")
            .withLibraryModification("org_brotli__dec", Libs.LibraryFields.AUTHOR_NAME, "Google")
            .supportFragment()

        // make sure that aboutLibrariesFragment can provide the view
        parentFragmentManager.beginTransaction()
            .add(aboutLibrariesFragment, null)
            .commit()


        binding.aboutTablayout.getTabAt(TAB_POSITION_TRANSLATIONS)?.view?.visibility = View.GONE

        binding.aboutTablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {

                binding.aboutLinearlayout.removeAllViews()

                when (tab?.position) {
                    TAB_POSITION_ABOUT -> binding.aboutLinearlayout.addView(bindingAboutJtx.root)
                    //TAB_POSITION_TRANSLATIONS -> null // TODO: replace with translations
                    TAB_POSITION_LIBRARIES -> binding.aboutLinearlayout.addView(aboutLibrariesFragment.requireView())
                    TAB_POSITION_THANKS -> binding.aboutLinearlayout.addView(bindingAboutThanks.root)
                    else -> binding.aboutLinearlayout.addView(bindingAboutJtx.root)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {  /* nothing to do */  }
            override fun onTabReselected(tab: TabLayout.Tab?) {  /* nothing to do */  }
        })

        // let the bee talk, just for fun ;-)
        var clickCount = 1
        bindingAboutJtx.aboutAppTechbeeLogo.setOnClickListener {

            val messages = arrayOf("If it's for coffee, then yes", "Bzzzz", "Bzzzzzzzzz", "I'm working here", "What's up?")
            if(clickCount%5 == 0)
                bindingAboutJtx.aboutAppTechbeeLogo.setImageResource(R.drawable.logo_techbee_front)
            else
                bindingAboutJtx.aboutAppTechbeeLogo.setImageResource(R.drawable.logo_techbee)
            Toast.makeText(requireContext(), messages[clickCount%5], Toast.LENGTH_SHORT).show()
            clickCount++
        }

        return binding.root
    }

    override fun onResume() {

        try {
            val activity = requireActivity() as MainActivity
            activity.setToolbarTitle(getString(R.string.toolbar_text_about), null)
        } catch(e: Exception) {
            Log.d("Cast not successful", e.toString())
            //This error will always happen for fragment testing, as the cast to Main Activity cannot be successful
        }

        super.onResume()
    }

}