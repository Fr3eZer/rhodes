require 'rhodes_analytics/analytics_object'

module RhodesAnalytics
  class Transaction < AnalyticsObject
    HITTYPE = "transaction"
    FIELD_MAPPING = {
      transaction_id: 'ti',
      affiliation: 'ta',
      revenue: 'tr',
      shipping: 'ts',
      tax: 'tt',
      currency: 'cu'
    }
  end
end