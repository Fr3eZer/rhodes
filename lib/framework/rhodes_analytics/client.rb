module RhodesAnalytics
  class Client
    
    def send(options)
      postdata = ""
      
      options.each do |k,v|
        postdata += k + "="
        
        # If value is a boolean convert it to a string
        if !!v == v
          v = convert_boolean(v)
        end
          
        postdata += v + "&"
      end
      
      # Delete "&" on end of postdata string
      postdata = postdata.chop
      
      http_post("www.google-analytics.com/collect", postdata)
    end
    
    def convert_boolean boolean
      boolean ? "1" : "0"
    end
    
    def http_post(url, data)
      puts "POSTDATA #{data}"
      response = Rho::Network.post(:url => url, :body => data)
      puts response
      unless response["status"] == "ok"
        puts "Status of request not ok"
      end
    end
    
  end
end