package at.bitfire.notesx5

import android.net.Uri
import android.provider.BaseColumns
import at.bitfire.notesx5.SyncContentProviderContract.X5ICalObject.CONTENT_URI_PATH_ICALOBJECT
import at.bitfire.notesx5.X5Attendee.CONTENT_URI_PATH_ATTENDEE
import at.bitfire.notesx5.X5Category.CONTENT_URI_PATH_CATEGORY
import at.bitfire.notesx5.X5Collection.CONTENT_URI_PATH_COLLECTION
import at.bitfire.notesx5.X5Comment.CONTENT_URI_PATH_COMMENT
import at.bitfire.notesx5.X5Contact.CONTENT_URI_PATH_CONTACT
import at.bitfire.notesx5.X5Organizer.CONTENT_URI_PATH_ORGANIZER
import at.bitfire.notesx5.X5Relatedto.CONTENT_URI_PATH_RELATEDTO
import at.bitfire.notesx5.X5Resource.CONTENT_URI_PATH_RESOURCE
import at.bitfire.notesx5.database.*
import at.bitfire.notesx5.database.properties.*

class SyncContentProviderContract {

    object NotesX5Contract {
        //private val sUriFactories: MutableMap<String, UriFactory?> = HashMap<String, UriFactory>(4)

        /**
         * URI parameter to signal that the caller is a sync adapter.
         */
        const val CALLER_IS_SYNCADAPTER = "caller_is_syncadapter"
        /**
         * URI parameter to submit the account name of the account we operate on.
         */
        const val ACCOUNT_NAME = "account_name"
        /**
         * URI parameter to submit the account type of the account we operate on.
         */
        const val ACCOUNT_TYPE = "account_type"

        const val SYNC_PROVIDER_AUTHORITY = "at.bitfire.notesx5.provider"



        private val URI_ICALOBJECT = Uri.parse("content://${this.SYNC_PROVIDER_AUTHORITY}/$CONTENT_URI_PATH_ICALOBJECT")
        private val URI_ATTENDEE = Uri.parse("content://${this.SYNC_PROVIDER_AUTHORITY}/$CONTENT_URI_PATH_ATTENDEE")
        private val URI_CATEGORY = Uri.parse("content://${this.SYNC_PROVIDER_AUTHORITY}/$CONTENT_URI_PATH_CATEGORY")
        private val URI_COMMENT = Uri.parse("content://${this.SYNC_PROVIDER_AUTHORITY}/$CONTENT_URI_PATH_COMMENT")
        private val URI_CONTACT = Uri.parse("content://${this.SYNC_PROVIDER_AUTHORITY}/$CONTENT_URI_PATH_CONTACT")
        private val URI_ORGANIZER = Uri.parse("content://${this.SYNC_PROVIDER_AUTHORITY}/$CONTENT_URI_PATH_ORGANIZER")
        private val URI_RELATEDTO = Uri.parse("content://${this.SYNC_PROVIDER_AUTHORITY}/$CONTENT_URI_PATH_RELATEDTO")
        private val URI_RESOURCE = Uri.parse("content://${this.SYNC_PROVIDER_AUTHORITY}/$CONTENT_URI_PATH_RESOURCE")
        private val URI_COLLECTION = Uri.parse("content://${this.SYNC_PROVIDER_AUTHORITY}/$CONTENT_URI_PATH_COLLECTION")

    }


    object X5ICalObject {

        /** The name of the the content URI for IcalObjects.
         * This is a general purpose table containing general columns
         * for Journals, Notes and Todos */
        const val CONTENT_URI_PATH_ICALOBJECT = "icalobject"

        /** The name of the ID column.
         * This is the unique identifier of an ICalObject
         * Type: [Long]*/
        const val COLUMN_ID = BaseColumns._ID

        /* The names of all the other columns  */
        /** The column for the component based on the values
         * provided in the enum [Component]
         * Type: [String]
         */
        const val COLUMN_COMPONENT = "component"

        /**
         * Purpose:  This column/property defines a short summary or subject for the calendar component.
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.1.12]
         * Type: [String]
         */
        const val COLUMN_SUMMARY = "summary"
        /**
         * Purpose:  This column/property provides a more complete description of the calendar component than that provided by the "SUMMARY" property.
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.1.5]
         * Type: [String]
         */
        const val COLUMN_DESCRIPTION = "description"
        /**
         * Purpose:  This column/property specifies when the calendar component begins.
         * The corresponding timezone is stored in [COLUMN_DTSTART_TIMEZONE].
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.2.4]
         * Type: [Long]
         */
        const val COLUMN_DTSTART = "dtstart"
        /**
         * Purpose:  This column/property specifies the timezone of when the calendar component begins.
         * The corresponding datetime is stored in [COLUMN_DTSTART].
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.2.4]
         * Type: [String]
         */
        const val COLUMN_DTSTART_TIMEZONE = "dtstarttimezone"
        /**
         * Purpose:  This column/property specifies when the calendar component ends.
         * The corresponding timezone is stored in [COLUMN_DTEND_TIMEZONE].
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.2.4]
         * Type: [Long]
         */
        const val COLUMN_DTEND = "dtend"
        /**
         * Purpose:  This column/property specifies the timezone of when the calendar component ends.
         * The corresponding datetime is stored in [COLUMN_DTEND].
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.2.2]
         * Type: [String]
         */
        const val COLUMN_DTEND_TIMEZONE = "dtendtimezone"
        /**
         * Purpose:  This property defines the overall status or confirmation for the calendar component.
         * The possible values of a status are defined in [StatusTodo] for To-Dos and in [StatusJournal] for Notes and Journals
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.1.11]
         * Type: [String]
         */
        const val COLUMN_STATUS = "status"
        /**
         * Purpose:  This property defines the access classification for a calendar component.
         * The possible values of a status are defined in the enum [Classification].
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.1.11]
         * Type: [String]
         */
        const val COLUMN_CLASSIFICATION = "classification"
        /**
         * Purpose:  This property defines a Uniform Resource Locator (URL) associated with the iCalendar object.
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.4.6]
         * Type: [String]
         */
        const val COLUMN_URL = "url"
        /**
         * Purpose:  This property is used to represent contact information or alternately a reference
         * to contact information associated with the calendar component.
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.4.2]
         * Type: [String]
         */
        const val COLUMN_CONTACT = "contact"
        /**
         * Purpose:  This property specifies information related to the global position for the activity specified by a calendar component.
         * This property is split in the fields [COLUMN_GEO_LAT] for the latitude
         * and [COLUMN_GEO_LONG] for the longitude coordinates.
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.1.6]
         * Type: [Float]
         */
        const val COLUMN_GEO_LAT = "geolat"
        /**
         * Purpose:  This property specifies information related to the global position for the activity specified by a calendar component.
         * This property is split in the fields [COLUMN_GEO_LAT] for the latitude
         * and [COLUMN_GEO_LONG] for the longitude coordinates.
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.1.6]
         * Type: [Float]
         */
        const val COLUMN_GEO_LONG = "geolong"
        /**
         * Purpose:  This property defines the intended venue for the activity defined by a calendar component.
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.1.7]
         * Type: [String]
         */
        const val COLUMN_LOCATION = "location"
        /**
         * Purpose:  This property is used by an assignee or delegatee of a to-do to convey the percent completion of a to-do to the "Organizer".
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.1.8]
         * Type: [Int]
         */
        const val COLUMN_PERCENT = "percent"
        /**
         * Purpose:  This property defines the relative priority for a calendar component.
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.1.9]
         * Type: [Int]
         */
        const val COLUMN_PRIORITY = "priority"
        /**
         * Purpose:  This property defines the date and time that a to-do is expected to be completed.
         * The corresponding timezone is stored in [COLUMN_DUE_TIMEZONE].
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.2.3]
         * Type: [Long]
         */
        const val COLUMN_DUE = "due"
        /**
         * Purpose:  This column/property specifies the timezone of when a to-do is expected to be completed.
         * The corresponding datetime is stored in [COLUMN_DUE].
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.2.2]
         * Type: [String]
         */
        const val COLUMN_DUE_TIMEZONE = "duetimezone"
        /**
         * Purpose:  This property defines the date and time that a to-do was actually completed.
         * The corresponding timezone is stored in [COLUMN_COMPLETED_TIMEZONE].
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.2.1]
         * Type: [Long]
         */
        const val COLUMN_COMPLETED = "completed"
        /**
         * Purpose:  This column/property specifies the timezone of when a to-do was actually completed.
         * The corresponding datetime is stored in [COLUMN_DUE].
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.2.1]
         * Type: [String]
         */
        const val COLUMN_COMPLETED_TIMEZONE = "completedtimezone"
        /**
         * Purpose:  This property specifies a positive duration of time.
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.2.5]
         * Type: [String]
         */
        const val COLUMN_DURATION = "duration"
        /**
         * Purpose:  This property defines the persistent, globally unique identifier for the calendar component.
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.4.7]
         * Type: [String]
         */
        const val COLUMN_UID = "uid"
        /**
         * Purpose:  This property specifies the date and time that the calendar information
         * was created by the calendar user agent in the calendar store.
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.7.1]
         * Type: [Long]
         */
        const val COLUMN_CREATED = "created"
        /**
         * Purpose:  In the case of an iCalendar object that specifies a
         * "METHOD" property, this property specifies the date and time that
         * the instance of the iCalendar object was created.  In the case of
         * an iCalendar object that doesn't specify a "METHOD" property, this
         * property specifies the date and time that the information
         * associated with the calendar component was last revised in the
         * calendar store.
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.7.2]
         * Type: [Long]
         */
        const val COLUMN_DTSTAMP = "dtstamp"
        /**
         * Purpose:  This property specifies the date and time that the information associated
         * with the calendar component was last revised in the calendar store.
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.7.3]
         * Type: [Long]
         */
        const val COLUMN_LAST_MODIFIED = "lastmodified"
        /**
         * Purpose:  This property defines the revision sequence number of the calendar component within a sequence of revisions.
         * See [https://tools.ietf.org/html/rfc5545#section-3.8.7.4]
         * Type: [Int]
         */
        const val COLUMN_SEQUENCE = "sequence"
        /**
         * Purpose:  This property specifies a color used for displaying the calendar, event, todo, or journal data.
         * See [https://tools.ietf.org/html/rfc7986#section-5.9]
         * Type: [String]
         */
        const val COLUMN_COLOR = "color"
        /**
         * Purpose:  This column is the foreign key to the [TABLE_NAME_COLLECTION].
         * Type: [Long]
         */
        const val COLUMN_ICALOBJECT_COLLECTIONID = "collectionId"
        /**
         * Purpose:  This column defines if a local collection was changed that is supposed to be synchronised.
         * Type: [Boolean]
         */
        const val COLUMN_DIRTY = "dirty"
        /**
         * Purpose:  This column defines if a collection that is supposed to be synchonized was locally marked as deleted.
         * Type: [Boolean]
         */
        const val COLUMN_DELETED = "deleted"

    }
}

object X5Attendee {

    /** The name of the the table for Attendees that are linked to an ICalObject.
     *  [https://tools.ietf.org/html/rfc5545#section-3.8.4.1] */
    const val CONTENT_URI_PATH_ATTENDEE = "attendee"

    /** The name of the ID column.
     * This is the unique identifier of an Attendee
     * Type: [Long] */
    const val COLUMN_ATTENDEE_ID = BaseColumns._ID

    /** The name of the Foreign Key Column for IcalObjects.
     * Type: [Long] */
    const val COLUMN_ATTENDEE_ICALOBJECT_ID = "icalObjectId"


/* The names of all the other columns  */

    /**
     * Purpose:  This value type is used to identify properties that contain a calendar user address (in this case of the attendee).
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.1] and [https://tools.ietf.org/html/rfc5545#section-3.3.3]
     * Type: [String]
     */
    const val COLUMN_ATTENDEE_CALADDRESS = "caladdress"

    /**
     * Purpose:  To identify the type of calendar user specified by the property in this case for the attendee.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.1] and [https://tools.ietf.org/html/rfc5545#section-3.2.3]
     * Type: [String]
     */
    const val COLUMN_ATTENDEE_CUTYPE = "cutype"

    /**
     * Purpose:  To specify the group or list membership of the calendar user specified by the property in this case for the attendee.
     * The possible values are defined in the enum [Cutype]
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.1] and [https://tools.ietf.org/html/rfc5545#section-3.2.11]
     * Type: [String]
     */
    const val COLUMN_ATTENDEE_MEMBER = "member"

    /**
     * Purpose:  To specify the participation role for the calendar user specified by the property in this case for the attendee.
     * The possible values are defined in the enum [Role]
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.1] and [https://tools.ietf.org/html/rfc5545#section-3.2.16]
     * Type: [String]
     */
    const val COLUMN_ATTENDEE_ROLE = "role"

    /**
     * Purpose:  To specify the participation status for the calendar user specified by the property in this case for the attendee.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.1] and [https://tools.ietf.org/html/rfc5545#section-3.2.12]
     * Type: [String]
     */
    const val COLUMN_ATTENDEE_PARTSTAT = "partstat"

    /**
     * Purpose:  To specify whether there is an expectation of a favor of a reply from the calendar user specified by the property value
     * in this case for the attendee.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.1] and [https://tools.ietf.org/html/rfc5545#section-3.2.17]
     * Type: [Boolean]
     */
    const val COLUMN_ATTENDEE_RSVP = "rsvp"

    /**
     * Purpose:  To specify the calendar users to whom the calendar user specified by the property
     * has delegated participation in this case for the attendee.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.1] and [https://tools.ietf.org/html/rfc5545#section-3.2.5]
     * Type: [String]
     */
    const val COLUMN_ATTENDEE_DELEGATEDTO = "delegatedto"

    /**
     * Purpose:  To specify the calendar users that have delegated their participation to the calendar user specified by the property
     * in this case for the attendee.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.1] and [https://tools.ietf.org/html/rfc5545#section-3.2.4]
     * Type: [String]
     */
    const val COLUMN_ATTENDEE_DELEGATEDFROM = "delegatedfrom"

    /**
     * Purpose:  To specify the calendar user that is acting on behalf of the calendar user specified by the property in this case for the attendee.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.1] and [https://tools.ietf.org/html/rfc5545#section-3.2.18]
     * Type: [String]
     */
    const val COLUMN_ATTENDEE_SENTBY = "sentby"

    /**
     * Purpose:  To specify the common name to be associated with the calendar user specified by the property in this case for the attendee.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.1] and [https://tools.ietf.org/html/rfc5545#section-3.2.18]
     * Type: [String]
     */
    const val COLUMN_ATTENDEE_CN = "cn"

    /**
     * Purpose:  To specify reference to a directory entry associated with the calendar user specified by the property in this case for the attendee.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.1] and [https://tools.ietf.org/html/rfc5545#section-3.2.2]
     * Type: [String]
     */
    const val COLUMN_ATTENDEE_DIR = "dir"

    /**
     * Purpose:  To specify the language for text values in a property or property parameter, in this case of the attendee.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.1] and [https://tools.ietf.org/html/rfc5545#section-3.2.10]
     * Type: [String]
     */
    const val COLUMN_ATTENDEE_LANGUAGE = "language"

    /**
     * Purpose:  To specify other properties for the attendee.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.1]
     * Type: [String]
     */
    const val COLUMN_ATTENDEE_OTHER = "other"

}


object X5Category {

    /** The name of the the table for Categories that are linked to an ICalObject.
     * [https://tools.ietf.org/html/rfc5545#section-3.8.1.2]*/
    const val CONTENT_URI_PATH_CATEGORY = "category"

    /** The name of the ID column for categories.
     * This is the unique identifier of a Category
     * Type: [Long]*/
    const val COLUMN_CATEGORY_ID = BaseColumns._ID

    /** The name of the Foreign Key Column for IcalObjects.
     * Type: [Long] */
    const val COLUMN_CATEGORY_ICALOBJECT_ID = "icalObjectId"


/* The names of all the other columns  */
    /**
     * Purpose:  This property defines the name of the category for a calendar component.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.1.2]
     * Type: [String]
     */
    const val COLUMN_CATEGORY_TEXT = "text"
    /**
     * Purpose:  To specify the language for text values in a property or property parameter, in this case of the category.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.1.2] and [https://tools.ietf.org/html/rfc5545#section-3.2.10]
     * Type: [String]
     */
    const val COLUMN_CATEGORY_LANGUAGE = "language"
    /**
     * Purpose:  To specify other properties for the category.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.1.2]
     * Type: [String]
     */
    const val COLUMN_CATEGORY_OTHER = "other"
}


object X5Comment {

    /** The name of the the table for Comments that are linked to an ICalObject.
     * [https://tools.ietf.org/html/rfc5545#section-3.8.1.4]*/
    const val CONTENT_URI_PATH_COMMENT = "comment"

    /** The name of the ID column for comments.
     * This is the unique identifier of a Comment
     * Type: [Long]*/
    const val COLUMN_COMMENT_ID = BaseColumns._ID

    /** The name of the Foreign Key Column for IcalObjects.
     * Type: [Long] */
    const val COLUMN_COMMENT_ICALOBJECT_ID = "icalObjectId"


/* The names of all the other columns  */
    /**
     * Purpose:  This property specifies non-processing information intended to provide a comment to the calendar user.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.1.4]
     * Type: [String]
     */
    const val COLUMN_COMMENT_TEXT = "text"
    /**
     * Purpose:  To specify the language for text values in a property or property parameter, in this case of the comment.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.1.4]
     * Type: [String]
     */
    const val COLUMN_COMMENT_ALTREP = "altrep"
    /**
     * Purpose:  To specify an alternate text representation for the property value, in this case of the comment.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.1.4]
     * Type: [String]
     */
    const val COLUMN_COMMENT_LANGUAGE = "language"
    /**
     * Purpose:  To specify other properties for the comment.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.1.4]
     * Type: [String]
     */
    const val COLUMN_COMMENT_OTHER = "other"
}

object X5Contact {

    /** The name of the the table for Contact that are linked to an ICalObject.
     * [https://tools.ietf.org/html/rfc5545#section-3.8.4.2]
     */
    const val CONTENT_URI_PATH_CONTACT = "contact"

    /** The name of the ID column for the contact.
     * This is the unique identifier of a Contact
     * Type: [Long]*/
    const val COLUMN_CONTACT_ID = BaseColumns._ID

    /** The name of the Foreign Key Column for IcalObjects.
     * Type: [Long] */
    const val COLUMN_CONTACT_ICALOBJECT_ID = "icalObjectId"


/* The names of all the other columns  */
    /**
     * Purpose:  This property defines the name of the contact for a calendar component.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.2]
     * Type: [String]
     */
    const val COLUMN_CONTACT_TEXT = "text"
    /**
     * Purpose:  To specify the language for text values in a property or property parameter, in this case of the contact.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.2] and [https://tools.ietf.org/html/rfc5545#section-3.2.10]
     * Type: [String]
     */
    const val COLUMN_CONTACT_LANGUAGE = "language"
    /**
     * Purpose:  To specify other properties for the contact.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.2]
     * Type: [String]
     */
    const val COLUMN_CONTACT_OTHER = "other"

}

object X5Organizer {
    /** The name of the the table for Organizer that are linked to an ICalObject.
     * [https://tools.ietf.org/html/rfc5545#section-3.8.4.3]
     */
    const val CONTENT_URI_PATH_ORGANIZER = "organizer"

    /** The name of the ID column for the organizer.
     * This is the unique identifier of a Organizer
     * Type: [Long]*/
    const val COLUMN_ORGANIZER_ID = BaseColumns._ID

    /** The name of the Foreign Key Column for IcalObjects.
     * Type: [Long] */
    const val COLUMN_ORGANIZER_ICALOBJECT_ID = "icalObjectId"


/* The names of all the other columns  */
    /**
     * Purpose:  This value type is used to identify properties that contain a calendar user address (in this case of the organizer).
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.3] and [https://tools.ietf.org/html/rfc5545#section-3.3.3]
     * Type: [String]
     */
    const val COLUMN_ORGANIZER_CALADDRESS = "caladdress"
    /**
     * Purpose:  To specify the common name to be associated with the calendar user specified by the property in this case for the organizer.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.3] and [https://tools.ietf.org/html/rfc5545#section-3.2.18]
     * Type: [String]
     */
    const val COLUMN_ORGANIZER_CN = "cnparam"
    /**
     * Purpose:  To specify reference to a directory entry associated with the calendar user specified by the property in this case for the organizer.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.3] and [https://tools.ietf.org/html/rfc5545#section-3.2.2]
     * Type: [String]
     */
    const val COLUMN_ORGANIZER_DIR = "dirparam"
    /**
     * Purpose:  To specify the calendar user that is acting on behalf of the calendar user specified by the property in this case for the organizer.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.3] and [https://tools.ietf.org/html/rfc5545#section-3.2.18]
     * Type: [String]
     */
    const val COLUMN_ORGANIZER_SENTBY = "sentbyparam"
    /**
     * Purpose:  To specify the language for text values in a property or property parameter, in this case of the organizer.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.3] and [https://tools.ietf.org/html/rfc5545#section-3.2.10]
     * Type: [String]
     */
    const val COLUMN_ORGANIZER_LANGUAGE = "language"
    /**
     * Purpose:  To specify other properties for the organizer.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.3]
     * Type: [String]
     */
    const val COLUMN_ORGANIZER_OTHER = "other"


}

object X5Relatedto {

    /** The name of the the table for Relationships (related-to) that are linked to an ICalObject.
     * [https://tools.ietf.org/html/rfc5545#section-3.8.4.5]
     */
    const val CONTENT_URI_PATH_RELATEDTO = "relatedto"

    /** The name of the ID column for the related-to.
     * This is the unique identifier of a Related-to
     * Type: [Long]*/
    const val COLUMN_RELATEDTO_ID = BaseColumns._ID

    /** The name of the Foreign Key Column for IcalObjects.
     * Type: [Long] */
    const val COLUMN_RELATEDTO_ICALOBJECT_ID = "icalObjectId"

    /** The name of the second Foreign Key Column of the related IcalObject
     * Type: [Long]
     */
    const val COLUMN_RELATEDTO_LINKEDICALOBJECT_ID = "linkedICalObjectId"


/* The names of all the other columns  */
    /**
     * Purpose:  This property is used to represent a relationship or reference between one calendar component and another.
     * The text gives the UID of the related calendar entry.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.5]
     * Type: [String]
     */
    const val COLUMN_RELATEDTO_TEXT = "text"
    /**
     * Purpose:  To specify the type of hierarchical relationship associated
     * with the calendar component specified by the property.
     * The possible relationship types are defined in the enum [Reltypeparam]
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.5] and [https://tools.ietf.org/html/rfc5545#section-3.2.15]
     * Type: [String]
     */
    const val COLUMN_RELATEDTO_RELTYPE = "reltype"
    /**
     * Purpose:  To specify other properties for the related-to.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.4.5]
     * Type: [String]
     */
    const val COLUMN_RELATEDTO_OTHER = "other"


}

object X5Resource {
    /** The name of the the table for Resources that are linked to an ICalObject.
     * [https://tools.ietf.org/html/rfc5545#section-3.8.1.10]*/
    const val CONTENT_URI_PATH_RESOURCE = "resource"

    /** The name of the ID column for resources.
     * This is the unique identifier of a Resource
     * Type: [Long]*/
    const val COLUMN_RESOURCE_ID = BaseColumns._ID

    /** The name of the Foreign Key Column for IcalObjects.
     * Type: [Long] */
    const val COLUMN_RESOURCE_ICALOBJECT_ID = "icalObjectId"


/* The names of all the other columns  */
    /**
     * Purpose:  This property defines the name of the resource for a calendar component.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.1.10]
     * Type: [String]
     */
    const val COLUMN_RESOURCE_TEXT = "text"
    /**
     * Purpose:  To specify the language for text values in a property or property parameter, in this case of the resource.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.1.10] and [https://tools.ietf.org/html/rfc5545#section-3.2.10]
     * Type: [String]
     */
    const val COLUMN_RESOURCE_RELTYPE = "reltype"
    /**
     * Purpose:  To specify other properties for the resource.
     * see [https://tools.ietf.org/html/rfc5545#section-3.8.1.10]
     * Type: [String]
     */
    const val COLUMN_RESOURCE_OTHER = "other"

}

object X5Collection {

    /** The name of the the table for Collections
     * ICalObjects MUST be linked to a collection! */
    const val CONTENT_URI_PATH_COLLECTION = "collection"


    // TODO !!!
}

/*
    @Synchronized
    private fun getUriFactory(authority: String): UriFactory? {
        var uriFactory: UriFactory? = sUriFactories[authority]
        if (uriFactory == null) {
            uriFactory = UriFactory(authority)
            uriFactory.addUri(org.dmfs.tasks.contract.TaskContract.SyncState.CONTENT_URI_PATH)
            uriFactory.addUri(TaskLists.CONTENT_URI_PATH)
            uriFactory.addUri(Tasks.CONTENT_URI_PATH)
            uriFactory.addUri(Tasks.SEARCH_URI_PATH)
            uriFactory.addUri(org.dmfs.tasks.contract.TaskContract.Instances.CONTENT_URI_PATH)
            uriFactory.addUri(org.dmfs.tasks.contract.TaskContract.Categories.CONTENT_URI_PATH)
            uriFactory.addUri(Alarms.CONTENT_URI_PATH)
            uriFactory.addUri(org.dmfs.tasks.contract.TaskContract.Properties.CONTENT_URI_PATH)
            sUriFactories[authority] = uriFactory
        }
        return uriFactory
    }

 */
