require 'rhodes_analytics/analytics_object'

module RhodesAnalytics
  class Event < AnalyticsObject
    HITTYPE = "event"
    FIELD_MAPPING = {
      category: "ec",
      action: "ea",
      label: "el",
      value: "ev"
    }
  end
end