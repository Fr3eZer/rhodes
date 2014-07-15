require 'rhodes_analytics/analytics_object'

module RhodesAnalytics
  class Timing < AnalyticsObject
    HITTYPE = "timing"
    FIELD_MAPPING = {
      category: 'utc',
      variable: 'utv',
      label: 'utl',
      time: 'utt',
      page_load_time: 'plt',
      dns_time: 'dns',
      page_download_time: 'pdt',
      redirect_response_time: 'rrt',
      tcp_connect_time: 'tcp',
      server_response_time: 'srt'
    }
  end
end