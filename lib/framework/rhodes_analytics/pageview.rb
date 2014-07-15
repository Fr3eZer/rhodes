require 'rhodes_analytics/analytics_object'

module RhodesAnalytics
  class Pageview < AnalyticsObject
    HITTYPE = "pageview"
    FIELD_MAPPING = {
      hostname: "dh",
      path: "dp",
      title: "dt"
    }
  end
end