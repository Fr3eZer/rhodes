require 'rhodes_analytics/analytics_object'

module RhodesAnalytics
  class Exception < AnalyticsObject
    HITTYPE = "exception"
    FIELD_MAPPING = {
      description: "exd",
      fatal: "exf"
    }
  end
end