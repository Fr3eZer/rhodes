module RhodesAnalytics
  class Helper
    
    def self.create_client_id
      SecureRandom.uuid
    end
    
  end
end