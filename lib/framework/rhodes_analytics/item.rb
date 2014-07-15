require 'rhodes_analytics/analytics_object'

module RhodesAnalytics
  class Item < AnalyticsObject
    HITTYPE = "item"
    FIELD_MAPPING = {
      name: "in",
      price: "ip",
      quantity: "iq",
      code: "ic",
      category: "iv",
      currency: "cu",
      transaction_id: 'ti'
    }
  end
end