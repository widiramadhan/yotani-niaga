package com.javaindoku.yotaniniaga.view.delivery.`interface`

import com.javaindoku.yotaniniaga.model.deliveryreservation.DeliveryReservationEditBody

interface DeliveryInterface {
    fun rescheduleDelivery(deliveryReservationEditBody: DeliveryReservationEditBody)
}