package at.bitfire.notesx5.ui

import android.app.AlertDialog
import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import at.bitfire.notesx5.*
import at.bitfire.notesx5.database.VJournalDatabase
import at.bitfire.notesx5.database.VJournalDatabaseDao
import at.bitfire.notesx5.databinding.FragmentVjournalItemEditBinding
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_vjournal_item_categories_chip.view.*
import java.util.*


class VJournalItemEditFragment : Fragment(), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    lateinit var binding: FragmentVjournalItemEditBinding
    lateinit var application: Application
    lateinit var dataSource: VJournalDatabaseDao
    lateinit var viewModelFactory:  VJournalItemEditViewModelFactory
    lateinit var vJournalItemEditViewModel: VJournalItemEditViewModel
    lateinit var inflater: LayoutInflater

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Get a reference to the binding object and inflate the fragment views.

        this.inflater = inflater
        this.binding = FragmentVjournalItemEditBinding.inflate(inflater, container, false)
        this.application = requireNotNull(this.activity).application

        this.dataSource = VJournalDatabase.getInstance(application).vJournalDatabaseDao

        val arguments = VJournalItemEditFragmentArgs.fromBundle((arguments!!))

        // add menu
        setHasOptionsMenu(true)


        this.viewModelFactory = VJournalItemEditViewModelFactory(arguments.item2edit, dataSource, application)
        vJournalItemEditViewModel =
                ViewModelProvider(
                        this, viewModelFactory).get(VJournalItemEditViewModel::class.java)

        binding.model = vJournalItemEditViewModel
        binding.lifecycleOwner = this



        vJournalItemEditViewModel.savingClicked.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                vJournalItemEditViewModel.vJournalItemUpdated.value!!.vJournalItem.summary = binding.summaryEdit.editText?.text.toString()
                vJournalItemEditViewModel.vJournalItemUpdated.value!!.vJournalItem.description = binding.descriptionEdit.editText?.text.toString()
                vJournalItemEditViewModel.vJournalItemUpdated.value!!.vJournalItem.collection = binding.collection.selectedItem.toString()
                vJournalItemEditViewModel.vJournalItemUpdated.value!!.vJournalItem.organizer = binding.organizerEdit.editText?.text.toString()
                vJournalItemEditViewModel.vJournalItemUpdated.value!!.vJournalItem.url = binding.urlEdit.editText?.text.toString()
                vJournalItemEditViewModel.vJournalItemUpdated.value!!.vJournalItem.attendee = binding.attendeeEdit.editText?.text.toString()
                vJournalItemEditViewModel.vJournalItemUpdated.value!!.vJournalItem.contact = binding.contactEdit.editText?.text.toString()
                vJournalItemEditViewModel.vJournalItemUpdated.value!!.vJournalItem.related = binding.relatedtoEdit.editText?.text.toString()

                vJournalItemEditViewModel.update()
            }
        })

        vJournalItemEditViewModel.deleteClicked.observe(viewLifecycleOwner, Observer {
            if (it == true) {

                // show Alert Dialog before the item gets really deleted
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Delete \"${vJournalItemEditViewModel.vJournalItem.value!!.vJournalItem.summary}\"")
                builder.setMessage("Are you sure you want to delete \"${vJournalItemEditViewModel.vJournalItem.value!!.vJournalItem.summary}\"?")
                builder.setPositiveButton("Delete") { _, _ ->
                    var summary = vJournalItemEditViewModel.vJournalItem.value!!.vJournalItem.summary
                    vJournalItemEditViewModel.delete()
                    Toast.makeText(context, "\"$summary\" successfully deleted.", Toast.LENGTH_LONG).show()
                    this.findNavController().navigate(VJournalItemEditFragmentDirections.actionVJournalItemEditFragmentToVjournalListFragmentList())
                }
                builder.setNegativeButton("Cancel") { _, _ ->
                    // Do nothing, just close the message
                }

                builder.setNeutralButton("Mark as cancelled") { _, _ ->
                    vJournalItemEditViewModel.vJournalItemUpdated.value!!.vJournalItem.status = 2    // 2 = CANCELLED
                    vJournalItemEditViewModel.savingClicked()

                    var summary = vJournalItemEditViewModel.vJournalItem.value!!.vJournalItem.summary
                    Toast.makeText(context, "\"$summary\" marked as Cancelled.", Toast.LENGTH_LONG).show()

                }

                builder.show()
            }
        })

        vJournalItemEditViewModel.returnVJournalItemId.observe(viewLifecycleOwner, Observer {
            if (it != 0L) {
                this.findNavController().navigate(VJournalItemEditFragmentDirections.actionVJournalItemEditFragmentToVjournalListFragmentList().setItem2focus(it))
            }
            vJournalItemEditViewModel.savingClicked.value = false
        })


        vJournalItemEditViewModel.vJournalItem.observe(viewLifecycleOwner, {

            // Add the chips for existing categories
            if (vJournalItemEditViewModel.vJournalItem.value != null)
                addChips(convertCategoriesCSVtoList(vJournalItemEditViewModel.vJournalItem.value!!.vJournalItem.categories))


            // Set the default value of the Status Chip
            val statusItems = resources.getStringArray(R.array.vjournal_status)
            if (vJournalItemEditViewModel.vJournalItem.value?.vJournalItem?.status == 3)      // if unsupported don't show the status
                binding.statusChip.visibility = View.GONE
            else
                binding.statusChip.text = statusItems[vJournalItemEditViewModel.vJournalItem.value!!.vJournalItem.status]   // if supported show the status according to the String Array


            // Set the default value of the Classification Chip
            val classificationItems = resources.getStringArray(R.array.vjournal_classification)
            if (vJournalItemEditViewModel.vJournalItem.value?.vJournalItem?.classification == 3)      // if unsupported don't show the classification
                binding.classificationChip.visibility = View.GONE
            else
                binding.classificationChip.text = classificationItems[vJournalItemEditViewModel.vJournalItem.value!!.vJournalItem.classification]  // if supported show the classification according to the String Array

            // set the default selection for the spinner. The same snippet exists for the allOrganizers observer
            if(vJournalItemEditViewModel.allCollections.value != null) {
                val selectedCollectionPos = vJournalItemEditViewModel.allCollections.value?.indexOf(vJournalItemEditViewModel.vJournalItem.value?.vJournalItem?.collection)
                if (selectedCollectionPos != null)
                    binding.collection.setSelection(selectedCollectionPos)
            }
        })



        // Set up items to suggest for categories
        vJournalItemEditViewModel.allCategories.observe(viewLifecycleOwner, {
            // Create the adapter and set it to the AutoCompleteTextView
            if (vJournalItemEditViewModel.allCategories.value != null) {
                val allCategoriesCSV = convertCategoriesListtoCSVString(vJournalItemEditViewModel.allCategories.value!!.toMutableList())
                val allCategoriesList = convertCategoriesCSVtoList(allCategoriesCSV).distinct()
                val arrayAdapter = ArrayAdapter<String>(application.applicationContext, android.R.layout.simple_list_item_1, allCategoriesList)
                binding.categoriesAddAutocomplete.setAdapter(arrayAdapter)
            }
        })

        vJournalItemEditViewModel.allCollections.observe(viewLifecycleOwner, {

            // set up the adapter for the organizer spinner
            val spinner: Spinner = binding.collection
            val adapter = ArrayAdapter<Any?>(context!!, android.R.layout.simple_spinner_item, vJournalItemEditViewModel.allCollections.value!!)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.setAdapter(adapter)

            // set the default selection for the spinner. The same snippet exists for the vJournalItem observer
            if(vJournalItemEditViewModel.allCollections.value != null) {
                val selectedCollectionPos = vJournalItemEditViewModel.allCollections.value?.indexOf(vJournalItemEditViewModel.vJournalItem.value?.vJournalItem?.collection)
                if (selectedCollectionPos != null)
                        spinner.setSelection(selectedCollectionPos)
            }

        })


        binding.dtstartTime.setOnClickListener {
            showDatepicker()
        }

        binding.dtstartYear.setOnClickListener {
            showDatepicker()
        }

        binding.dtstartMonth.setOnClickListener {
            showDatepicker()
        }

        binding.dtstartDay.setOnClickListener {
            showDatepicker()
        }

        // Transform the category input into a chip when the Add-Button is clicked
        // If the user entered multiple categories separated by comma, the values will be split in multiple categories

        binding.categoriesAdd.setEndIconOnClickListener {
            // Respond to end icon presses
            val addedCategories: List<String> = convertCategoriesCSVtoList(binding.categoriesAdd.editText?.text.toString())
            addChips(addedCategories)
            binding.categoriesAdd.editText?.text?.clear()
        }



        // Transform the category input into a chip when the Done button in the keyboard is clicked
        binding.categoriesAdd.editText?.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    val addedCategories: List<String> = convertCategoriesCSVtoList(binding.categoriesAdd.editText?.text.toString())
                    addChips(addedCategories)
                    binding.categoriesAdd.editText?.text?.clear()
                    true
                }
                else -> false
            }
        }



        binding.statusChip.setOnClickListener {

            val statusItems = resources.getStringArray(R.array.vjournal_status)
            val checkedStatus = vJournalItemEditViewModel.vJournalItem.value!!.vJournalItem.status

            MaterialAlertDialogBuilder(context!!)
                    //.setTitle(resources.getString(R.string.title))
                    .setTitle("Set status")
                    .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                        // Respond to neutral button press
                        vJournalItemEditViewModel.vJournalItemUpdated.value!!.vJournalItem.status = vJournalItemEditViewModel.vJournalItem.value!!.vJournalItem.status  // Reset to previous status
                        binding.statusChip.text = statusItems[checkedStatus]   // don't forget to update the UI
                    }
                    .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                        // Respond to positive button press, ATTENTION: "which" returns here -1 as this is the INT-value of the positive button!
                    }
                    // Single-choice items (initialized with checked item)
                    .setSingleChoiceItems(statusItems, checkedStatus) { dialog, which ->
                        // Respond to item chosen
                        vJournalItemEditViewModel.vJournalItemUpdated.value!!.vJournalItem.status = which
                        binding.statusChip.text = statusItems[which]     // don't forget to update the UI
                    }
                    .show()
        }




        binding.classificationChip.setOnClickListener {

            val classificationItems = resources.getStringArray(R.array.vjournal_classification)
            val checkedClassification = vJournalItemEditViewModel.vJournalItem.value!!.vJournalItem.classification

            MaterialAlertDialogBuilder(context!!)
                    //.setTitle(resources.getString(R.string.title))
                    .setTitle("Set classification")
                    .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->
                        // Respond to neutral button press
                        vJournalItemEditViewModel.vJournalItemUpdated.value!!.vJournalItem.classification = vJournalItemEditViewModel.vJournalItem.value!!.vJournalItem.classification  // Reset to previous classification
                        binding.classificationChip.text = classificationItems[checkedClassification]   // don't forget to update the UI
                    }
                    .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                        // Respond to positive button press
                    }
                    // Single-choice items (initialized with checked item)
                    .setSingleChoiceItems(classificationItems, checkedClassification) { dialog, which ->
                        // Respond to item chosen
                        vJournalItemEditViewModel.vJournalItemUpdated.value!!.vJournalItem.classification = which
                        binding.classificationChip.text = classificationItems[which]     // don't forget to update the UI
                    }
                    .show()
        }




        binding.urlEdit.editText?.setOnFocusChangeListener { view, hasFocus ->
                if((!binding.urlEdit.editText?.text.isNullOrEmpty() && !isValidURL(binding.urlEdit.editText?.text.toString())))
                    vJournalItemEditViewModel.urlError.value = "Please enter a valid URL"

        }



        return binding.root
    }


    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {

        val c = Calendar.getInstance()
        c.timeInMillis = vJournalItemEditViewModel.vJournalItemUpdated.value?.vJournalItem?.dtstart!!

        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, day)
        //var formattedDate = convertLongToDateString(c.timeInMillis)
        //Log.println(Log.INFO, "OnTimeSet", "Here are the values: $formattedDate")    }

        binding.dtstartYear.text = convertLongToYearString(c.timeInMillis)
        binding.dtstartMonth.text = convertLongToMonthString(c.timeInMillis)
        binding.dtstartDay.text = convertLongToDayString(c.timeInMillis)

        vJournalItemEditViewModel.vJournalItemUpdated.value!!.vJournalItem.dtstart = c.timeInMillis

        showTimepicker()

    }


    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val c = Calendar.getInstance()
        c.timeInMillis = vJournalItemEditViewModel.vJournalItemUpdated.value?.vJournalItem?.dtstart!!
        c.set(Calendar.HOUR_OF_DAY, hourOfDay)
        c.set(Calendar.MINUTE, minute)

        //var formattedTime = convertLongToTimeString(c.timeInMillis)
        //Log.println(Log.INFO, "OnTimeSet", "Here are the values: $formattedTime")

        binding.dtstartTime.text = convertLongToTimeString(c.timeInMillis)

        vJournalItemEditViewModel.vJournalItemUpdated.value!!.vJournalItem.dtstart = c.timeInMillis

    }




    fun showDatepicker() {
        val c = Calendar.getInstance()
        c.timeInMillis = vJournalItemEditViewModel.vJournalItemUpdated.value?.vJournalItem?.dtstart!!

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(activity!!, this, year, month, day).show()
    }

    fun showTimepicker() {
        val c = Calendar.getInstance()
        c.timeInMillis = vJournalItemEditViewModel.vJournalItemUpdated.value?.vJournalItem?.dtstart!!

        val hourOfDay = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        TimePickerDialog(activity, this, hourOfDay, minute, is24HourFormat(activity)).show()
    }


    fun addChips(categories: List<String>) {

        categories.forEach() { category ->

            if (category == "")
                return@forEach

            vJournalItemEditViewModel.categoriesListChanged.add(category)

            val categoryChip = inflater.inflate(R.layout.fragment_vjournal_item_edit_categories_chip, binding.categoriesChipgroup, false) as Chip
            categoryChip.text = category
            binding.categoriesChipgroup.addView(categoryChip)

            categoryChip.setOnClickListener {
                // Responds to chip click
            }

            categoryChip.setOnCloseIconClickListener { chip ->
                // Responds to chip's close icon click if one is present
                vJournalItemEditViewModel.categoriesListChanged.remove(category)
                chip.visibility = View.GONE
            }

            categoryChip.setOnCheckedChangeListener { chip, isChecked ->
                // Responds to chip checked/unchecked
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_vjournal_item_edit, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.vjournal_item_delete) {
            vJournalItemEditViewModel.deleteClicked()
        }
        return super.onOptionsItemSelected(item)
    }
}

