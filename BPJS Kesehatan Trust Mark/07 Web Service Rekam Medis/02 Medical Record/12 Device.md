Format : Json

Content-Type: application/json; charset=utf-8

Sumber : https://www.hl7.org/fhir/Device.html

    {
	"resource": [
		{
		   "resourceType":"Device",
		   "id":"0901R001-1180006-12-ddd99da0-3916-446e-8429-207c22738496",
		   "text":{
			  "status":"generated",
			  "div":
                    "Generated Narrative with Details
                    
                    id: 0901R001-1180006-12-ddd99da0-3916-446e-8429-207c22738496
                    
                    identifier: MDVx024590
                    
                    type: SKINTACT EASYTAB (Details : {http://acme.com/devices code = MDVx024590, given as SKINTACT EASYTAB})
                    
                    lotNumber:
                    
                    manufacturer:
                    
                    model:
                    
                    patient: Patient/0901R001-1180006-12-f37a5d7f-3d36-4897-952c-49dac09efc71
                    
                    contact:"
            },
               "identifier":[
                  {
                     "system":"http://acme.com/devices/pacemakers/octane/serial",
                     "value":"MDVx024590"
                  }
               ],
               "type":{
                  "coding":[
                     {
                        "system":"http://acme.com/devices",
                        "code":"MDVx024590",
                        "display":"SKINTACT EASYTAB"
                     }
                  ]
               },
               "lotNumber":"",
               "manufacturer":"",
               "manufactureDate":"",
               "expirationDate":"",
               "model":"",
               "patient":{
                  "reference":"Patient/0901R001-1180006-12-f37a5d7f-3d36-4897-952c-49dac09efc71"
               },
               "contact":[
                  {
                     "system":"phone",
                     "value":"ext 4352",
                     "use":"work"
                  }
               ]
            },
            {
               "resourceType":"Device",
               "id":"0901R001-1180006-12-a620a1d0-3926-4533-90b0-bc002f1d3b47",
               "text":{
                  "status":"generated",
                  "div":
                        "Generated Narrative with Details
                        
                        id: 0901R001-1180006-12-a620a1d0-3926-4533-90b0-bc002f1d3b47
                        
                        identifier: MDVx001160
                        
                        type: PEN NEEDLE 31 GA BD (Details : {http://acme.com/devices code = MDVx001160, given as PEN NEEDLE 31 GA BD})
                        
                        lotNumber: ALKES
                        
                        manufacturer: ANUGRAH ARGON MEDICA, PT
                        
                        model:
                        
                        patient: Patient/0901R001-1180006-12-f37a5d7f-3d36-4897-952c-49dac09efc71
                        
                        contact: 021 3861271"
                },
               "identifier":[
                  {
                     "system":"http://acme.com/devices/pacemakers/octane/serial",
                     "value":"MDVx001160"
                  }
               ],
               "type":{
                  "coding":[
                     {
                        "system":"http://acme.com/devices",
                        "code":"MDVx001160",
                        "display":"PEN NEEDLE 31 GA BD"
                     }
                  ]
               },
               "lotNumber":"ALKES",
               "manufacturer":"ANUGRAH ARGON MEDICA, PT",
               "manufactureDate":"",
               "expirationDate":"",
               "model":"",
               "patient":{
                  "reference":"Patient/0901R001-1180006-12-f37a5d7f-3d36-4897-952c-49dac09efc71"
               },
               "contact":[
                  {
                     "system":"phone",
                     "value":"021 3861271",
                     "use":"work"
                  }
               ]
            }
        ]
    }