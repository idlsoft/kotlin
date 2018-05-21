package test

import kotlinx.android.parcel.*
import android.os.Parcelable

@Parcelize
class A(val firstName: String) : Parcelable {
    val <warning descr="[PLUGIN_WARNING] Property would not be serialized into a 'Parcel'. Add '@IgnoredOnParcel' annotation to remove the warning">secondName</warning>: String = ""

    val <warning descr="[PLUGIN_WARNING] Property would not be serialized into a 'Parcel'. Add '@IgnoredOnParcel' annotation to remove the warning">delegated</warning> by lazy { "" }

    lateinit var <warning descr="[PLUGIN_WARNING] Property would not be serialized into a 'Parcel'. Add '@IgnoredOnParcel' annotation to remove the warning">lateinit</warning>: String

    val customGetter: String
        get() = ""

    var customSetter: String
        get() = ""
        set(<warning descr="[UNUSED_PARAMETER] Parameter 'v' is never used">v</warning>) {}
}

@Parcelize
@Suppress("WRONG_ANNOTATION_TARGET_WITH_USE_SITE_TARGET")
class B(@IgnoredOnParcel val firstName: String) : Parcelable {
    @IgnoredOnParcel
    var a: String = ""

    fun foo(<error descr="[PLUGIN_ERROR] '@IgnoredOnParcel' is only applicable to class properties"><error descr="[WRONG_ANNOTATION_TARGET] This annotation is not applicable to target 'value parameter'">@IgnoredOnParcel</error></error> <warning descr="[UNUSED_PARAMETER] Parameter 'a' is never used">a</warning>: String) {}
}

class NonParcelable(@<error descr="[PLUGIN_ERROR] Class 'NonParcelable' should be annotated with '@Parcelize'">IgnoredOnParcel</error> val firstName: String) {
    @<error descr="[PLUGIN_ERROR] Class 'NonParcelable' should be annotated with '@Parcelize'">IgnoredOnParcel</error>
    var a: String = ""
}

<error descr="[PLUGIN_ERROR] '@IgnoredOnParcel' is only applicable to class properties"><error descr="[WRONG_ANNOTATION_TARGET_WITH_USE_SITE_TARGET] This annotation is not applicable to target 'top level property with backing field' and use site target '@field'">@field:IgnoredOnParcel</error></error>
var topLevel: String = ""