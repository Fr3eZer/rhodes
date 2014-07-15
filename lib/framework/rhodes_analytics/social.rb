require 'rhodes_analytics/analytics_object'

module RhodesAnalytics
  class Social < AnalyticsObject
    HITTYPE = "social"
    FIELD_MAPPING = {
      action: "sa",
      network: "sn",
      target: "st"
    }
  end
end