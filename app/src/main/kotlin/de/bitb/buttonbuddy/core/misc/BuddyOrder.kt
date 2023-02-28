package de.bitb.buttonbuddy.core.misc

sealed class BuddyOrder(val orderType: OrderType) {
    class Date(orderType: OrderType) : BuddyOrder(orderType)
    class Name(orderType: OrderType) : BuddyOrder(orderType)

    fun copy(orderType: OrderType): BuddyOrder =
        when (this) {
            is Date -> Date(orderType)
            is Name -> Name(orderType)
        }
}

sealed class OrderType {
    object Ascending : OrderType()
    object Descending : OrderType()
}