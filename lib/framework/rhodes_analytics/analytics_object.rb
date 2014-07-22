require 'rhodes_analytics/client'

module RhodesAnalytics
  class AnalyticsObject
    GLOBAL_FIELD_MAPPING = {
       anonymize_ip: 'aip',
       queue_time: 'qt',
       cache_buster: 'z',
       user_id: 'uid',
       
       # Session
       user_ip: 'uip',
       user_agent: 'ua',
       session_control: 'sc',

       # Traffic Sources
       referrer: 'dr',
       campaign_name: 'cn',
       campaign_source: 'cs',
       campaign_medium: 'cm',
       campaign_keyword: 'ck',
       campaign_content: 'cc',
       campaign_id: 'ci',
       adwords_id: 'gclid',
       display_ads_id: 'dclid',

       # System Info
       screen_resolution: 'sr',
       viewport_size: 'vp',
       screen_colors: 'sd',
       user_language: 'ul',
       java_enabled: 'je',
       flash_version: 'fl',
       non_interactive: 'ni',
       document_location: 'dl',
       document_encoding: 'de',
       document_hostname: 'dh',
       document_path: 'dp',
       document_title: 'dt',
       screen_name: 'cd',
       link_id: 'linkid',

       # App Tracking
       application_name: 'an',
       application_id: 'aid',
       application_installer_id: 'aiid',
       application_version: 'av',

       # Content Experiments
       experiment_id: 'xid',
       experiment_variant: 'xvar'
     }

    def initialize options_hash
      @options = {}
      options_hash.each do |k,v|
        if GLOBAL_FIELD_MAPPING.has_key? k
          @options[GLOBAL_FIELD_MAPPING[k]] = v
        elsif self.class::FIELD_MAPPING.has_key? k
          @options[self.class::FIELD_MAPPING[k]] = v
        else
          puts "Mapping does not exist!"
        end
      end
    end
    
    def track tracking_id, client_id
      @options['tid'] = tracking_id
      @options['cid'] = client_id
      @options['v'] = "1";
      @options['t'] = self.class::HITTYPE;
      client = RhodesAnalytics::Client.new()
      client.send(@options)
    end
    
  end
end