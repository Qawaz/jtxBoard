package at.bitfire.notesx5.ui

import android.app.Application
import android.text.Editable
import android.util.Log
import androidx.lifecycle.*
import at.bitfire.notesx5.convertCategoriesListtoCSVString
import at.bitfire.notesx5.database.VJournalDatabaseDao
import at.bitfire.notesx5.database.vJournalItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class VJournalItemEditViewModel(    private val vJournalItemId: Long,
                                val database: VJournalDatabaseDao,
                                application: Application) : AndroidViewModel(application) {

    lateinit var vJournalItem: LiveData<vJournalItem?>
    lateinit var allCategories: LiveData<List<String>>
    //lateinit var allOrganizers: LiveData<List<String>>
    lateinit var allCollections: LiveData<List<String>>

    lateinit var dateVisible: LiveData<Boolean>
    lateinit var timeVisible: LiveData<Boolean>

    var returnVJournalItemId: MutableLiveData<Long> = MutableLiveData<Long>().apply { postValue(0L) }
    var savingClicked: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { postValue(false) }
    var deleteClicked: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { postValue(false) }

    lateinit var vJournalItemUpdated: MutableLiveData<vJournalItem>

    var summaryChanged: String = ""
    var descriptionChanged: String = ""
    var statusChanged: Int = -1
    var classificationChanged: Int = -1
    var organizerChanged: String = ""
    var collectionChanged: String = ""

    var urlChanged: String = ""
    var attendeeChanged: String = ""
    var contactChanged: String = ""
    var relatesChanged: String = ""


    var dtstartChangedYear: Int = -1
    var dtstartChangedMonth: Int = -1
    var dtstartChangedDay: Int = -1
    var dtstartChangedHour: Int = -1
    var dtstartChangedMinute: Int = -1

    var categoriesListChanged: MutableList<String> = mutableListOf()

    val urlError = MutableLiveData<String>()



    init {

        viewModelScope.launch() {

            // insert a new value to initialize the vJournalItem or load the existing one from the DB
            vJournalItem = if (vJournalItemId == 0L)
                MutableLiveData<vJournalItem?>().apply {
                    postValue(vJournalItem()) }
            else
                database.get(vJournalItemId)

//            vJournalItemUpdated = vJournalItem as MutableLiveData<vJournalItem>

            allCategories = database.getAllCategories()
            allCollections = database.getAllCollections()



            dateVisible = Transformations.map(vJournalItem) { item ->
                return@map item?.component == "JOURNAL"           // true if component == JOURNAL
            }

            timeVisible = Transformations.map(vJournalItem) { item ->
                if (item?.dtstart == 0L || item?.component != "JOURNAL" )
                    return@map false

                val minute_formatter = SimpleDateFormat("mm")
                val hour_formatter = SimpleDateFormat("HH")

                if (minute_formatter.format(Date(item!!.dtstart)).toString() == "00" && hour_formatter.format(Date(item.dtstart)).toString() == "00")
                    return@map false

                return@map true
            }

        }
    }



    fun savingClicked() {
        savingClicked.value = true
    }

    fun deleteClicked() {
        deleteClicked.value = true
    }

    fun update() {
        viewModelScope.launch() {
            var vJournalItemUpdate = vJournalItem.value!!.copy()
            vJournalItemUpdate.summary = summaryChanged
            vJournalItemUpdate.description = descriptionChanged
            vJournalItemUpdate.url = urlChanged
            vJournalItemUpdate.attendee = attendeeChanged
            vJournalItemUpdate.contact = contactChanged
            vJournalItemUpdate.related = relatesChanged
            vJournalItemUpdate.organizer = organizerChanged
            vJournalItemUpdate.collection = collectionChanged

            if(statusChanged >= 0) vJournalItemUpdate.status = statusChanged
            if(classificationChanged >= 0) vJournalItemUpdate.classification = classificationChanged

            vJournalItemUpdate.lastModified = System.currentTimeMillis()
            vJournalItemUpdate.dtstamp = System.currentTimeMillis()

            var c: Calendar = Calendar.getInstance()
            c.timeInMillis = vJournalItemUpdate.dtstart
            Log.println(Log.INFO, "VJournalItemEditViewMod", "Value before: ${c.timeInMillis}")
            if (dtstartChangedYear >= 0)
                c.set(Calendar.YEAR, dtstartChangedYear)
            if (dtstartChangedMonth >= 0)
                c.set(Calendar.MONTH, dtstartChangedMonth)
            if (dtstartChangedYear >= 0)
                c.set(Calendar.DAY_OF_MONTH, dtstartChangedDay)
            if (dtstartChangedHour >= 0)
                c.set(Calendar.HOUR_OF_DAY, dtstartChangedHour)
            if (dtstartChangedMinute >= 0)
                c.set(Calendar.MINUTE, dtstartChangedMinute)
            Log.println(Log.INFO, "VJournalItemEditViewMod", "Value after: ${c.timeInMillis}")

            vJournalItemUpdate.dtstart = c.timeInMillis

            categoriesListChanged = categoriesListChanged.sorted().toMutableList()
            vJournalItemUpdate.categories = convertCategoriesListtoCSVString(categoriesListChanged)


            if (vJournalItemUpdate.id == 0L) {
                //Log.println(Log.INFO, "VJournalItemViewModel", "creating a new one")
                returnVJournalItemId.value = database.insert(vJournalItemUpdate)
                //Log.println(Log.INFO, "vJournalItemViewModel", vJournalItemUpdate.id.toString())
            }
            else {
                returnVJournalItemId.value = vJournalItemUpdate.id
                vJournalItemUpdate.sequence++
                database.update(vJournalItemUpdate)
            }
        }
    }

    fun delete () {
        viewModelScope.launch(Dispatchers.IO) {
            database.delete(vJournalItem.value!!)
        }
    }

    fun clearUrlError(s: Editable) {
        urlError.value = null
    }

}



