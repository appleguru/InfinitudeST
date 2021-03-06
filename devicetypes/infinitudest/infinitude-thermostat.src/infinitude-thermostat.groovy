/*

Copyright 2015 SmartThings

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
in compliance with the License. You may obtain a copy of the License at: http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
for the specific language governing permissions and limitations under the License.
(Based on) Ecobee Thermostat

Author: zraken, swerb73
Date: 2020.04.23

*/

metadata {
    definition(name: "Infinitude Thermostat", namespace: "InfinitudeST", author: "SmartThingsMod") {
        capability "Actuator"
        capability "Thermostat"
        capability "Temperature Measurement"
        capability "Sensor"
        capability "Refresh"
        capability "Relative Humidity Measurement"
        capability "Health Check"

        command "generateEvent"
        command "resumeProgram"
        command "switchMode"
        command "switchFanMode"
        command "lowerHeatingSetpoint"
        command "raiseHeatingSetpoint"
        command "lowerCoolSetpoint"
        command "raiseCoolSetpoint"
        command "setProfHome"
        command "setProfAway"
        command "setProfSleep"
        command "setProfAwake"
        command "setProfAuto"
        // To satisfy some SA/rules that incorrectly using poll instead of Refresh
        command "poll"
        command "profileUpdate"

        attribute "thermostat", "string"
        attribute "maxCoolingSetpoint", "number"
        attribute "minCoolingSetpoint", "number"
        attribute "maxHeatingSetpoint", "number"
        attribute "minHeatingSetpoint", "number"
        attribute "deviceTemperatureUnit", "string"
        attribute "deviceAlive", "enum", ["true", "false"]
        attribute "thermostatSchedule", "string"
        attribute "outsideAirTemp", "number"
        attribute "holdStatus", "string"
        attribute "holdUntil", "string"
        attribute "outsideTemp", "number"
        attribute "zoneId", "string"
        attribute "profAutoActive", "string"
        attribute "profHomeActive", "string"
        attribute "profAwayActive", "string"
        attribute "profSleepActive", "string"
        attribute "profAwakeActive", "string"
    }

    tiles {
        multiAttributeTile(name: "temperature", type: "thermostat", width: 6, height: 4) {
            tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
                attributeState("temperature", label: '${currentValue}°', unit:"dF", defaultState: true
                  //icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/temperature-256.png",
                )
            }
            tileAttribute("device.humidity", key: "SECONDARY_CONTROL") {
                attributeState "humidity", label: '${currentValue}%', unit:"%", defaultState: true
                icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/humidity2-256.png"
            }
            tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
                attributeState("idle", backgroundColor:"#28DC22")
                attributeState("heating", backgroundColor:"#e86d13")
                attributeState("cooling", backgroundColor:"#00A0DC")
            }
            tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE") {
                attributeState("off", label:'off')
                attributeState("heat", label:'heat')
                attributeState("cool", label:'cool')
                attributeState("auto", label:'auto')
            }
            tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
                attributeState("heatingSetpoint", label:'${currentValue}', unit:"dF", defaultState: true)
            }
            tileAttribute("device.coolingSetpoint", key: "COOLING_SETPOINT") {
                attributeState("coolingSetpoint", label:'${currentValue}', unit:"dF", defaultState: true)
            }
        }

        standardTile("profileSet1", "device.profHomeActive", width: 1, height: 1, inactiveLabel: false, decoration: "flat") {
            state "yes", /*label: 'Home',*/ action: "setProfHome", backgroundColor: "#20cc20",
                icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/athome256.png"
            state "no", /*label: 'Home',*/ action: "setProfHome", backgroundColor: "#ffffff",
                icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/athome256.png"
            state "active", /*label: 'Home',*/ action: "setProfHome", backgroundColor: "#a0c0ff",
                icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/athome256.png"
        }
        standardTile("profileSet2", "device.profAwayActive", width: 1, height: 1, inactiveLabel: false, decoration: "flat") {
            state "yes", /*label: 'Away',*/ action: "setProfAway", backgroundColor: "#20cc20",
                icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/away256.png"
            state "no", /*label: 'Away',*/ action: "setProfAway", backgroundColor: "#ffffff",
                icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/away256.png"                
            state "active", /*label: 'Away',*/ action: "setProfAway", backgroundColor: "#a0c0ff",
                icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/away256.png"                
        }
        standardTile("profileSet3", "device.profSleepActive", width: 1, height: 1, inactiveLabel: false, decoration: "flat") {
            state "yes", /*label: 'Sleep',*/ action: "setProfSleep", backgroundColor: "#20cc20",
                icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/sleep256.png"
            state "no", /*label: 'Sleep',*/ action: "setProfSleep", backgroundColor: "#ffffff",
                icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/sleep256.png"
            state "active", /*label: 'Sleep',*/ action: "setProfSleep", backgroundColor: "#a0c0ff",
                icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/sleep256.png"
        }
        standardTile("profileSet4", "device.profAwakeActive", width: 1, height: 1, inactiveLabel: false, decoration: "flat") {
            state "yes", /*label: 'Awake',*/ action: "setProfAwake", backgroundColor: "#20cc20",
                icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/wake138.png"
            state "no", /*label: 'Awake',*/ action: "setProfAwake", backgroundColor: "#ffffff",
                icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/wake138.png"
            state "active", /*label: 'Awake',*/ action: "setProfAwake", backgroundColor: "#a0c0ff",
                icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/wake138.png"
        }
        standardTile("profileSet5", "device.profAutoActive", width: 1, height: 1, inactiveLabel: false, decoration: "flat") {
            state "yes", /*label: 'Res',*/ action: "setProfAuto", backgroundColor: "#20cc20", icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/resume.png"
            state "no", /*label: 'Res',*/ action: "setProfAuto", backgroundColor: "#ffffff", icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/resume.png"
        }
        valueTile("profileSet6", "device.profManualActive", width: 1, height: 1, inactiveLabel: false, decoration: "flat") {
            state "yes", label: 'M', backgroundColor: "#20cc20"/*, icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/resume.png"*/
            state "no", label: 'M', backgroundColor: "#ffffff"/*, icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/resume.png"*/
        }

        standardTile("raiseHeatingSetpoint", "device.heatingSetpoint", width: 2, height: 1, decoration: "flat") {
            state "heatingSetpoint", label: 'Heat Up', action: "raiseHeatingSetpoint", backgroundColor: "#ffffff", icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/thermostat-up.png"
        }
        standardTile("lowerHeatingSetpoint", "device.heatingSetpoint", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
            state "heatingSetpoint", label: 'Heat Down', action: "lowerHeatingSetpoint", backgroundColor: "#ffffff", icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/thermostat-down.png"
        }
        valueTile("thermostat", "device.thermostat", width: 2, height: 1, decoration: "flat") {
            state "thermostat", label: 'Activity:\n${currentValue}', backgroundColor: "#ffffff"
        }
        standardTile("raiseCoolSetpoint", "device.heatingSetpoint", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
            state "heatingSetpoint", label: 'Cool Up', action: "raiseCoolSetpoint", backgroundColor: "#ffffff", icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/thermostat-up.png"
        }
        standardTile("lowerCoolSetpoint", "device.coolingSetpoint", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
            state "coolingSetpoint", label: 'Cool Down', action: "lowerCoolSetpoint", backgroundColor: "#ffffff", icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/thermostat-down.png"
        }
        valueTile("heatingSetpoint", "device.heatingSetpoint", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
            state "heatingSetpoint", label: '${currentValue}°', backgroundColor: "#e86d13"
        }

        valueTile("coolingSetpoint", "device.coolingSetpoint", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
            state "coolingSetpoint", label: '${currentValue}°', backgroundColor: "#00a0dc"
        }

        standardTile("refresh", "device.refresh", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
            state "default", label: 'Refresh', action: "refresh.refresh" , backgroundColor: "#ffffff", icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/refresh.png"
        }
        valueTile("fanMode", "device.thermostatFanMode", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
            state "fanMode", label: 'Fan Mode:\n${currentValue}', backgroundColor: "ffffff"
        }
        valueTile("holdStatus", "device.thermostatHoldStatus", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
            state "holdStatus", label: 'Hold:\n${currentValue}', backgroundColor: "#ffffff"
        }
        valueTile("holdUntil", "device.thermostatHoldUntil", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
            state "holdUntil", label: 'Hold Until:\n${currentValue}', backgroundColor: "#ffffff"
        }
        valueTile("damperPosition", "device.damperPosition", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
            state "damperPosition", label: 'Damper:\n${currentValue}', backgroundColor: "#ffffff"
        }

        // Not Displaying These        
        standardTile("resumeProgram", "device.resumeProgram", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
            state "resume", action: "resumeProgram", nextState: "updating", label: 'Resume', icon: "st.samsung.da.oven_ic_send"
            state "updating", label: "Working", icon: "st.secondary.secondary"
        }
        standardTile("mode", "device.thermostatMode", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
            state "off", action: "switchMode", nextState: "updating"/*, label: 'Off'*/, icon: "st.thermostat.heating-cooling-off"
            state "heat", action: "switchMode", nextState: "updating"/*, label: 'Heat'*/, icon: "st.thermostat.heat"/*, backgroundColor: "#e86d13" */
            state "cool", action: "switchMode", nextState: "updating"/*, label: 'Cool'*/, icon: "st.thermostat.cool"/*, backgroundColor: "#00a0dc" */
            state "auto", action: "switchMode", nextState: "updating"/*, label: 'Auto'*/, icon: "st.thermostat.auto"/*, backgroundColor: "#20cc20" */
            state "fanonly", action: "switchMode", nextState: "updating"/*, label: 'FanOnly'*/, icon: "st.thermostat.heating-cooling-off"
            state "updating", label: "Updating..."/*, icon: "st.secondary.secondary"*/
        }
        
        // Not Displaying These   
        valueTile("outsideTemp", "device.outsideAirTemp", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
            state "outsideTemp", label: '${currentValue}° outside', backgroundColor: "#ffffff", icon: "st.Weather.weather14"
        }
        valueTile("zoneId", "device.zoneId", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
            state "zoneId", label: 'Zone ID:\n${currentValue}', backgroundColor: "#ffffff"
        }
        standardTile("thermostatSchedule", "device.thermostatSchedule", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
            state "home", action: "profileUpdate", label: 'Profile: Home', nextState: "changing", backgroundColor: "#ffffff", icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/home4-icn.png"
            state "away", action: "profileUpdate", label: 'Profile: Away', nextState: "changing", backgroundColor: "#ffffff", icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/home3-icn.png"
            state "sleep", action: "profileUpdate", label: 'Profile: Sleep', nextState: "changing", backgroundColor: "#ffffff", icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/bedroom2-icn.png"
            state "wake", action: "profileUpdate", label: 'Profile: Wake', nextState: "changing", backgroundColor: "#ffffff" , icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/health7-icn.png"
            state "manual", action: "profileUpdate", label: 'Profile: Manual', nextState: "changing", backgroundColor: "#ffffff", icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/preferences-tile.png" 
            state "auto", action: "profileUpdate", label: 'Profile: Auto', nextState: "changing", backgroundColor: "#ffffff", icon: "https://raw.githubusercontent.com/zraken/InfinitudeST/master/resources/home1-icn.png"
            state "changing", label: 'Updating...' /*, icon: "st.motion.motion.active" */
        }
        // END Not Displaying These

        main "temperature"
        details(["temperature",
        	"profileSet1", "profileSet2", "profileSet3", "profileSet4", "profileSet6", "profileSet5",
        	"refresh", "raiseHeatingSetpoint", "raiseCoolSetpoint",
            "mode", "heatingSetpoint", "coolingSetpoint", "thermostat", "lowerHeatingSetpoint",
            "lowerCoolSetpoint", "fanMode", "holdStatus", "holdUntil", "damperPosition" 
        ])
    }

    preferences {
        input title: "", description: "Infinitude Thermostat Device Handler. Advanced Troubleshooting info, Zone ID: ${zoneId}, Damper Position: ${damperPosition}.", displayDuringSetup: false, type: "paragraph", element: "paragraph", required: true

		//input "holdType", "enum", title: "Hold Type",
        // description: "When changing temperature, use Temporary (Until next transition) or Permanent hold (default)",
        // required: false, options:["Temporary", "Permanent"]
        //input "deadbandSetting", "number", title: "Minimum temperature difference between the desired Heat and Cool " +
        // "temperatures in Auto mode:\nNote! This must be the same as configured on the thermostat",
        // description: "temperature difference °F", defaultValue: 5,
        // required: false
    }

}

def installed() {
    // The device refreshes every 5 minutes by default so if we miss 2 refreshes we can consider it offline
    // Using 12 minutes because in testing, device health team found that there could be "jitter"
    sendEvent(name: "checkInterval", value: 60 * 12, data: [protocol: "cloud"], displayed: false)
    state.modes = ["home":"profHomeActive", "away":"profAwayActive", "sleep":"profSleepActive", "wake":"profAwakeActive", "auto":"profAutoActive", "manual":"profManualActive"]	
    for(mode in state.modes) {
        sendEvent([name: mode.getValue(), value: "no"])    
    }
    //sendEvent([name: "thermostatSchedule", value: "auto"])    
}

// Device Watch will ping the device to proactively determine if the device has gone offline
// If the device was online the last time we refreshed, trigger another refresh as part of the ping.
def ping() {
    def isAlive = device.currentValue("deviceAlive") == "true" ? true : false
    if (isAlive) {
        refresh()
    }
}

// parse events into attributes
def parse(String description) {
    log.debug "Parsing ‘${description}'"
}

def refresh() {
    log.debug "refresh"
    sendEvent([name: "thermostat", value: "updating"])
    poll2()
}
void poll() {}
void poll2() {
    log.debug "Executing poll using parent SmartApp"
    //log.debug "Id: " + device.id + ", Name: " + device.name + ", Label: " + device.label + ", NetworkId: " + device.deviceNetworkId
    //parent.refreshChild(device.deviceNetworkId)
    parent.syncSystem()
}

def setProfile(nextProfile) {
    sendEvent([name: "thermostat", value: "updating"])

    def currentProfile = device.currentValue("thermostatSchedule")
    def currentZone = device.currentValue("zoneId")
    log.debug "Profile Update for Zone : " + currentZone + " from: " + currentProfile + " to: " + nextProfile
    parent.changeProfile(currentZone, nextProfile)
    sendEvent([name: "thermostatSchedule", value: nextProfile])
    //def modes = ["home":"profHomeActive", "away":"profAwayActive", "sleep":"profSleepActive", "wake":"profAwakeActive", "auto":"profAutoActive"]	
    for(mode in state.modes) {
        if(mode.getKey() == nextProfile) {
            sendEvent([name: mode.getValue(), value: "yes"])    
        }
        else {
            sendEvent([name: mode.getValue(), value: "no"])    
        }         
    }
    runIn(15, "refresh", [overwrite: true])
}
def setProfHome() {
	setProfile("home")
}
def setProfAway() {
	setProfile("away")
}
def setProfAwake() {
	setProfile("wake")
}
def setProfSleep() {
	setProfile("sleep")
}
def setProfAuto() {
	setProfile("auto")
}

def profileUpdate() {
    sendEvent([name: "thermostat", value: "updating"])
    def currentProfile = device.currentValue("thermostatSchedule")
    def currentZone = device.currentValue("zoneId")
    def nextProfile = "changing"

    log.debug "-- Entering Profile Update -- " + currentProfile

    if (currentProfile == "manual") {
        nextProfile = "home"
    }
    if (currentProfile == "home") {
        nextProfile = "away"
    }
    if (currentProfile == "away") {
        nextProfile = "sleep"
    }
    if (currentProfile == "sleep") {
        nextProfile = "awake"
    }
    if (currentProfile == "awake") {
        nextProfile = "auto"
    }
    if (currentProfile == "auto") {
        nextProfile = "home"
    }    

    log.debug "Profile Update for Zone : " + currentZone + " from: " + currentProfile + " to: " + nextProfile
    parent.changeProfile(currentZone, nextProfile)
    sendEvent([name: "thermostatSchedule", value: nextProfile])
    runIn(15, "refresh", [overwrite: true])
}

def zUpdate(temp, operStatus, hum, hsp, csp, fan, currSched, oat, hold, otmr, damperposition, zoneid) {
    log.debug "zupdate: " + temp + ", " + operStatus + ", " + hum + ", " + hsp + ", " + csp + ", " + fan + ", " + currSched + ", " + oat + ", " + hold + ", " + otmr + ", " + damperposition + ", " + zoneid
    def oldSched = device.currentValue("thermostatSchedule")
    def oldHold = device.currentValue("thermostatHoldStatus")
    /*
    def updates = ["temperature":temp, "thermostat":operStatus, "heatingSetpoint":hsp, "coolingSetpoint":csp,
        "thermostatFanMode":fan, "outsideAirTemp":oat, "thermostatSchedule":currSched, "thermostatHoldStatus":hold,
        "thermostatHoldUntil":otmr, "damperPosition":damperposition, "zoneId":zoneid, "humidity":hum]
    */
    sendEvent([name: "temperature", value: temp, unit: "F"])
    sendEvent([name: "thermostat", value: operStatus]) //DUPLICATE - not needed
    sendEvent([name: "heatingSetpoint", value: hsp])
    sendEvent([name: "coolingSetpoint", value: csp])
    sendEvent([name: "thermostatFanMode", value: fan])
    sendEvent([name: "outsideAirTemp", value: oat])
    sendEvent([name: "thermostatSchedule", value: currSched])
    sendEvent([name: "thermostatHoldStatus", value: hold])
    sendEvent([name: "thermostatHoldUntil", value: otmr])
    sendEvent([name: "damperPosition", value: damperposition])
    sendEvent([name: "zoneId", value: zoneid])
    //hum = "Indoor humidity " + hum + "%\nOutside temp " + oat + "°"
    sendEvent([name: "humidity", value: hum])
    //def modes = ["home":"profHomeActive", "away":"profAwayActive", "sleep":"profSleepActive", "wake":"profAwakeActive", "auto":"profAutoActive", "manual":"profManualActive"]
    if(currSched != oldSched || oldHold != hold) {
        state.modes.each{ modex ->
                sendEvent([name: modex.getValue(), value: "no"])    
        }
        if(hold == "off") {
            //mode = Auto so mark this active profile in blue
            sendEvent([name: state.modes.get(currSched), value: "active"])
            sendEvent([name: "profAutoActive", value: "yes"])
        }
        else {
            //mode = Hold so mark this active profile in green
            sendEvent([name: state.modes.get(currSched), value: "yes"])
        }
    }
    sendEvent([name: "thermostatOperatingState", value: operStatus]) //idle/heating/cooling
    //ZZZZZ TODO
    //sendEvent([name: "thermostatMode", value: "heat"]) //off/auto/heat/cool
}
def generateEvent(Map results) {
    if (results) {
        def linkText = getLinkText(device)
        def supportedThermostatModes = ["off"]
        def thermostatMode = null
        def locationScale = getTemperatureScale()

        results.each {
            name,
            value ->
            def event = [name: name, linkText: linkText, handlerName: name]
            def sendValue = value

            if (name == "temperature" || name == "heatingSetpoint" || name == "coolingSetpoint") {
                sendValue = getTempInLocalScale(value, "F") // API return temperature values in F
                event << [value: sendValue, unit: locationScale]
            } else if (name == "maxCoolingSetpoint" || name == "minCoolingSetpoint" || name == "maxHeatingSetpoint" || name == "minHeatingSetpoint") {
                // Old attributes, keeping for backward compatibility
                sendValue = getTempInLocalScale(value, "F") // API return temperature values in F
                event << [value: sendValue, unit: locationScale, displayed: false]
                // Store min/max setpoint in device unit to avoid conversion rounding error when updating setpoints
                device.updateDataValue(name + "Fahrenheit", "${value}")
            } else if (name == "heatMode" || name == "coolMode" || name == "autoMode" || name == "auxHeatMode") {
                if (value == true) {
                    supportedThermostatModes << ((name == "auxHeatMode") ? "emergency heat" : name - "Mode")
                }
                return // as we don't want to send this event here, proceed to next name/value pair
            } else if (name == "thermostatFanMode") {
                sendEvent(name: "supportedThermostatFanModes", value: fanModes(), displayed: false)
                event << [value: value, data: [supportedThermostatFanModes: fanModes()]]
            } else if (name == "humidity") {
                event << [value: value, displayed: false, unit: "%"]
            } else if (name == "deviceAlive") {
                event['displayed'] = false
            } else if (name == "thermostatMode") {
                thermostatMode = (value == "auxHeatOnly") ? "emergency heat" : value.toLowerCase()
                return // as we don't want to send this event here, proceed to next name/value pair
            } else {
                event << [value: value.toString()]
            }
            event << [descriptionText: getThermostatDescriptionText(name, sendValue, linkText)]
            sendEvent(event)
        }
        if (state.supportedThermostatModes != supportedThermostatModes) {
            state.supportedThermostatModes = supportedThermostatModes
            sendEvent(name: "supportedThermostatModes", value: supportedThermostatModes, displayed: false)
        }
        if (thermostatMode) {
            sendEvent(name: "thermostatMode", value: thermostatMode, data: [supportedThermostatModes: state.supportedThermostatModes], linkText: linkText,
                descriptionText: getThermostatDescriptionText("thermostatMode", thermostatMode, linkText), handlerName: "thermostatMode")
        }
        generateSetpointEvent()
        generateStatusEvent()
    }
}

//return descriptionText to be shown on mobile activity feed
private getThermostatDescriptionText(name, value, linkText) {
    if (name == "temperature") {
        return "temperature is {value}°{location.temperatureScale}"

    } else if (name == "heatingSetpoint") {
        return "heating setpoint is ${value}°${location.temperatureScale}"

    } else if (name == "coolingSetpoint") {
        return "cooling setpoint is ${value}°${location.temperatureScale}"

    } else if (name == "thermostatMode") {
        return "thermostat mode is ${value}"

    } else if (name == "thermostatFanMode") {
        return "thermostat fan mode is ${value}"

    } else if (name == "humidity") {
        return "humidity is ${value} %"
    } else {
        return "${name} = ${value}"
    }
}

void setHeatingSetpoint(setpoint) {
    log.debug "***setHeatingSetpoint($setpoint)"
    
    sendEvent([name: "thermostat", value: "updating"])
    
    if (setpoint) {
        state.heatingSetpoint = setpoint.toDouble()
        runIn(15, "updateSetpoints", [overwrite: true])
    }
}

def setCoolingSetpoint(setpoint) {
    log.debug "***setCoolingSetpoint($setpoint)"

    sendEvent([name: "thermostat", value: "updating"])

    if (setpoint) {
        state.coolingSetpoint = setpoint.toDouble()
    }
    
    runIn(15, "updateSetpoints", [overwrite: true])
}

def updateSetpoints() {
    def deviceScale = "F" //API return/expects temperature values in F
    def data = [targetHeatingSetpoint: null, targetCoolingSetpoint: null]
    def heatingSetpoint = getTempInLocalScale("heatingSetpoint")
    def coolingSetpoint = getTempInLocalScale("coolingSetpoint")
    if (state.heatingSetpoint) {
        data = enforceSetpointLimits("heatingSetpoint", [targetValue: state.heatingSetpoint,
            heatingSetpoint: heatingSetpoint, coolingSetpoint: coolingSetpoint
        ])
    }
    if (state.coolingSetpoint) {
        heatingSetpoint = data.targetHeatingSetpoint ? getTempInLocalScale(data.targetHeatingSetpoint, deviceScale) : heatingSetpoint
        coolingSetpoint = data.targetCoolingSetpoint ? getTempInLocalScale(data.targetCoolingSetpoint, deviceScale) : coolingSetpoint
        data = enforceSetpointLimits("coolingSetpoint", [targetValue: state.coolingSetpoint,
            heatingSetpoint: heatingSetpoint, coolingSetpoint: coolingSetpoint
        ])
    }
    state.heatingSetpoint = null
    state.coolingSetpoint = null
    updateSetpoint(data)
}

void resumeProgram() {
    log.debug "resumeProgram() is called"

    sendEvent("name": "thermostat", "value": "resuming schedule", "description": statusText, displayed: false)
    def deviceId = device.deviceNetworkId.split(/\./).last()
    if (parent.resumeProgram(deviceId)) {
        sendEvent("name": "thermostat", "value": "setpoint is updating", "description": statusText, displayed: false)
        sendEvent("name": "resumeProgram", "value": "resume", descriptionText: "resumeProgram is done", displayed: false, isStateChange: true)
    } else {
        sendEvent("name": "thermostat", "value": "failed resume click refresh", "description": statusText, displayed: false)
        log.error "Error resumeProgram() check parent.resumeProgram(deviceId)"
    }
    //xyzrunIn(5, refresh, [overwrite: true])
}

def modes() {
    //xxxxx return state.supportedThermostatModes
    ["off", "heat", "cool", "auto", "fanonly"]
}

def fanModes() {
    // Ecobee does not report its supported fanModes; use hard coded values
    ["on", "auto"]
}

def switchSchedule() {
    //TODO
}
def switchMode() {
	sendEvent([name: "thermostat", value: "updating"])

    def currentMode = device.currentValue("thermostatMode")
    def modeOrder = modes()
    if (modeOrder) {
        def next = {
            modeOrder[modeOrder.indexOf(it) + 1] ?: modeOrder[0]
        }
        def nextMode = next(currentMode)
        switchToMode(nextMode)
    } else {
        log.warn "supportedThermostatModes not defined"
    }
}

def switchToMode(mode) {
    log.debug "switchToMode: ${mode}"
    parent.setMode(mode)
    sendEvent([name: "thermostatMode", value: mode])
    runIn(15, refresh, [overwrite: true])

    /*****
    def deviceId = device.deviceNetworkId.split(/./).last()
    // Thermostat's mode for "emergency heat" is "auxHeatOnly"
    if (!(parent.setMode(((mode == "emergency heat") ? "auxHeatOnly" : mode), deviceId))) {
        log.warn "Error setting mode:$mode"
        // Ensure the DTH tile is reset
        generateModeEvent(device.currentValue("thermostatMode"))
    }
    *****/
    //XYZ runIn(5, refresh, [overwrite: true])
}

def switchFanMode() {
    def currentFanMode = device.currentValue("thermostatFanMode")
    def fanModeOrder = fanModes()
    def next = {
        fanModeOrder[fanModeOrder.indexOf(it) + 1] ?: fanModeOrder[0]
    }
    switchToFanMode(next(currentFanMode))
}

def switchToFanMode(fanMode) {
    log.debug "switchToFanMode: $fanMode"
    def heatingSetpoint = getTempInDeviceScale("heatingSetpoint")
    def coolingSetpoint = getTempInDeviceScale("coolingSetpoint")
    def deviceId = device.deviceNetworkId.split(/./).last()
    def sendHoldType = holdType ? ((holdType == "Temporary") ? "nextTransition" : "indefinite") : "indefinite"

    if (!(parent.setFanMode(heatingSetpoint, coolingSetpoint, deviceId, sendHoldType, fanMode))) {
        log.warn "Error setting fanMode:fanMode"
        // Ensure the DTH tile is reset
        generateFanModeEvent(device.currentValue("thermostatFanMode"))
    }
    //XYZ runIn(5, refresh, [overwrite: true])
}

def getDataByName(String name) {
    state[name] ?: device.getDataValue(name)
}

def setThermostatMode(String mode) {
    log.debug "setThermostatMode($mode)"
    def supportedModes = modes()
    if (supportedModes) {
        mode = mode.toLowerCase()
        def modeIdx = supportedModes.indexOf(mode)
        if (modeIdx < 0) {
            log.warn("Thermostat mode $mode not valid for this thermostat")
            return
        }
        mode = supportedModes[modeIdx]
        switchToMode(mode)
    } else {
        log.warn "supportedThermostatModes not defined"
    }
}

def setThermostatFanMode(String mode) {
    log.debug "setThermostatFanMode($mode)"
    mode = mode.toLowerCase()
    def supportedFanModes = fanModes()
    def modeIdx = supportedFanModes.indexOf(mode)
    if (modeIdx < 0) {
        log.warn("Thermostat fan mode $mode not valid for this thermostat")
        return
    }
    mode = supportedFanModes[modeIdx]
    switchToFanMode(mode)
}

def generateModeEvent(mode) {
    sendEvent(name: "thermostatMode", value: mode, data: [supportedThermostatModes: device.currentValue("supportedThermostatModes")],
        isStateChange: true, descriptionText: "device.displayName is in {mode} mode")
}

def generateFanModeEvent(fanMode) {
    sendEvent(name: "thermostatFanMode", value: fanMode, data: [supportedThermostatFanModes: device.currentValue("supportedThermostatFanModes")],
        isStateChange: true, descriptionText: "device.displayName fan is in {fanMode} mode")
}

def generateOperatingStateEvent(operatingState) {
    sendEvent(name: "thermostatOperatingState", value: operatingState, descriptionText: "device.displayName is {operatingState}", displayed: true)
}

def off() {
    setThermostatMode("off")
}
def heat() {
    setThermostatMode("heat")
}
def emergencyHeat() {
    setThermostatMode("emergency heat")
}
def cool() {
    setThermostatMode("cool")
}
def auto() {
    setThermostatMode("auto")
}

def fanOn() {
    setThermostatFanMode("on")
}
def fanAuto() {
    setThermostatFanMode("auto")
}
def fanCirculate() {
    setThermostatFanMode("circulate")
}

// =============== Setpoints ===============
def generateSetpointEvent() {
    def mode = device.currentValue("thermostatMode")
    def setpoint = getTempInLocalScale("heatingSetpoint") // (mode == "heat") || (mode == "emergency heat")
    def coolingSetpoint = getTempInLocalScale("coolingSetpoint")

    if (mode == "cool") {
        setpoint = coolingSetpoint
    } else if ((mode == "auto") || (mode == "off")) {
        setpoint = roundC((setpoint + coolingSetpoint) / 2)
    } // else (mode == "heat") || (mode == "emergency heat")
    sendEvent("name": "thermostatSetpoint", "value": setpoint, "unit": location.temperatureScale)
}

//setPointStr = "heatingSetpoint" or "coolingSetpoint"/
//raisebool = true or false
def raiseLowerHeatCoolSetpoint(setPointStr, raisebool) {
	sendEvent([name: "thermostat", value: "updating"])
    alterSetpoint(raisebool, setPointStr)
    def setpoint = getTempInLocalScale(setPointStr)
    def currentZone = device.currentValue("zoneId")
    log.debug /*(raisebool)?"Raising ":"Lowering " +*/ setPointStr + " on Zone: " + currentZone + " to: " + setpoint
    if(setPointStr == "heatingSetpoint") {
        parent.changeHtsp(currentZone, setpoint)
    }
    else {
        parent.changeClsp(currentZone, setpoint)
    }
    state.modes.each{ key, value -> 
        (key == "manual") ? sendEvent([name: value, value: "yes"])
            : sendEvent([name: value, value: "no"])
    }
    runIn(15, "refresh", [overwrite: true])
}
def raiseHeatingSetpoint() {
    raiseLowerHeatCoolSetpoint("heatingSetpoint", true)
}
def lowerHeatingSetpoint() {
    raiseLowerHeatCoolSetpoint("heatingSetpoint", false)
}
def raiseCoolSetpoint() {
    raiseLowerHeatCoolSetpoint("coolingSetpoint", true)
}
def lowerCoolSetpoint() {
    raiseLowerHeatCoolSetpoint("coolingSetpoint", false)
}
/*
def raiseHeatingSetpoint() {
	sendEvent([name: "thermostat", value: "updating"])
    sendEvent([name: "temperature", value: "Updating..."])
    alterSetpoint(true, "heatingSetpoint")
    def heatingSetpoint = getTempInLocalScale("heatingSetpoint")
    def currentZone = device.currentValue("zoneId")
    log.debug "Raising Htsp on Zone: " + currentZone + " to: " + heatingSetpoint
    parent.changeHtsp(currentZone, heatingSetpoint)
    runIn(15, "refresh", [overwrite: true])
}

def lowerHeatingSetpoint() {
    sendEvent([name: "thermostat", value: "updating"])
    sendEvent([name: "temperature", value: "Updating..."])
    alterSetpoint(false, "heatingSetpoint")
    def heatingSetpoint = getTempInLocalScale("heatingSetpoint")
    def currentZone = device.currentValue("zoneId")
    log.debug "Lowering Htsp on Zone: " + currentZone + " to: " + heatingSetpoint
    parent.changeHtsp(currentZone, heatingSetpoint)
    runIn(15, "refresh", [overwrite: true])
}

def raiseCoolSetpoint() {
    sendEvent([name: "thermostat", value: "updating"])
    sendEvent([name: "temperature", value: "Updating..."])
    alterSetpoint(true, "coolingSetpoint")
    def coolingSetpoint = getTempInLocalScale("coolingSetpoint")
    def currentZone = device.currentValue("zoneId")
    log.debug "Raising Csp on Zone: " + currentZone + " to: " + coolingSetpoint
    parent.changeClsp(currentZone, coolingSetpoint)
    runIn(15, "refresh", [overwrite: true])
}

def lowerCoolSetpoint() {
    sendEvent([name: "thermostat", value: "updating"])
    sendEvent([name: "temperature", value: "Updating..."])
    alterSetpoint(false, "coolingSetpoint")
    def coolingSetpoint = getTempInLocalScale("coolingSetpoint")
    def currentZone = device.currentValue("zoneId")
    log.debug "Lowering Csp on Zone: " + currentZone + " to: " + coolingSetpoint
    parent.changeClsp(currentZone, coolingSetpoint)
    runIn(15, "refresh", [overwrite: true])
}
*/

// Adjusts nextHeatingSetpoint either .5° C/1° F) if raise true/false
def alterSetpoint(raise, setpoint) {
    // don't allow setpoint change if thermostat is off
    if (device.currentValue("thermostatMode") == "off") {
        return
    }
    def locationScale = getTemperatureScale()
    def deviceScale = "F"
    def heatingSetpoint = getTempInLocalScale("heatingSetpoint")
    def coolingSetpoint = getTempInLocalScale("coolingSetpoint")
    def targetValue = (setpoint == "heatingSetpoint") ? heatingSetpoint : coolingSetpoint
    def delta = (locationScale == "F") ? 1 : 0.5
    targetValue += raise ? delta : -delta

    def data = enforceSetpointLimits(setpoint, [targetValue: targetValue, heatingSetpoint: heatingSetpoint, coolingSetpoint: coolingSetpoint], raise)
    // update UI without waiting for the device to respond, this to give user a smoother UI experience
    // also, as runIn's have to overwrite and user can change heating/cooling setpoint separately separate runIn's have to be used
    if (data.targetHeatingSetpoint) {
        sendEvent("name": "heatingSetpoint", "value": getTempInLocalScale(data.targetHeatingSetpoint, deviceScale),
            unit: getTemperatureScale(), eventType: "ENTITY_UPDATE", displayed: false)
    }
    if (data.targetCoolingSetpoint) {
        sendEvent("name": "coolingSetpoint", "value": getTempInLocalScale(data.targetCoolingSetpoint, deviceScale),
            unit: getTemperatureScale(), eventType: "ENTITY_UPDATE", displayed: false)
    }
    //no idea why this is needed: runIn(5, "updateSetpoint", [data: data, overwrite: true])
}

def enforceSetpointLimits(setpoint, data, raise = null) {
    def locationScale = getTemperatureScale()
    def minSetpoint = (setpoint == "heatingSetpoint") ? device.getDataValue("minHeatingSetpointFahrenheit") : device.getDataValue("minCoolingSetpointFahrenheit")
    def maxSetpoint = (setpoint == "heatingSetpoint") ? device.getDataValue("maxHeatingSetpointFahrenheit") : device.getDataValue("maxCoolingSetpointFahrenheit")
    minSetpoint = minSetpoint ? Double.parseDouble(minSetpoint) : ((setpoint == "heatingSetpoint") ? 45 : 65) // default 45 heat, 65 cool
    maxSetpoint = maxSetpoint ? Double.parseDouble(maxSetpoint) : ((setpoint == "heatingSetpoint") ? 79 : 92) // default 79 heat, 92 cool
    def deadband = deadbandSetting ? deadbandSetting : 5 // °F
    def delta = (locationScale == "F") ? 1 : 0.5
    def targetValue = getTempInDeviceScale(data.targetValue, locationScale)
    def heatingSetpoint = getTempInDeviceScale(data.heatingSetpoint, locationScale)
    def coolingSetpoint = getTempInDeviceScale(data.coolingSetpoint, locationScale)
    // Enforce min/mix for setpoints
    if (targetValue > maxSetpoint) {
        targetValue = maxSetpoint
    } else if (targetValue < minSetpoint) {
        targetValue = minSetpoint
    } else if ((raise != null) && ((setpoint == "heatingSetpoint" && targetValue == heatingSetpoint) ||
            (setpoint == "coolingSetpoint" && targetValue == coolingSetpoint))) {
        // Ensure targetValue differes from old. When location scale differs from device,
        // converting between C -> F -> C may otherwise result in no change.
        targetValue += raise ? delta : -delta
    }
    // Enforce deadband between setpoints
    if (setpoint == "heatingSetpoint") {
        heatingSetpoint = targetValue
        coolingSetpoint = (heatingSetpoint + deadband > coolingSetpoint) ? heatingSetpoint + deadband : coolingSetpoint
    }
    if (setpoint == "coolingSetpoint") {
        coolingSetpoint = targetValue
        heatingSetpoint = (coolingSetpoint - deadband < heatingSetpoint) ? coolingSetpoint - deadband : heatingSetpoint
    }
    return [targetHeatingSetpoint: heatingSetpoint, targetCoolingSetpoint: coolingSetpoint]
}

def updateSetpoint(data) {
    def deviceId = device.deviceNetworkId.split(/\./).last()
    def sendHoldType = holdType ? ((holdType == "Temporary") ? "nextTransition" : "indefinite") : "indefinite"

    /* if (parent.setHold(data.targetHeatingSetpoint, data.targetCoolingSetpoint, deviceId, sendHoldType)) {
    log.debug "alterSetpoint succeed to change setpoints:${data}"
    } else {
    log.error "Error alterSetpoint"
    }
    */

    //XYZ runIn(5, refresh, [overwrite: true])
}

def generateStatusEvent() {
    def mode = device.currentValue("thermostatMode")
    def heatingSetpoint = device.currentValue("heatingSetpoint")
    def coolingSetpoint = device.currentValue("coolingSetpoint")
    def temperature = device.currentValue("temperature")
    def statusText = "Right Now: Idle"
    def operatingState = "idle"

    if (mode == "heat" || mode == "emergency heat") {
        if (temperature < heatingSetpoint) {
            statusText = "Heating to ${heatingSetpoint}°${location.temperatureScale}"
            operatingState = "heating"
        }
    } else if (mode == "cool") {
        if (temperature > coolingSetpoint) {
            statusText = "Cooling to ${coolingSetpoint}°${location.temperatureScale}"
            operatingState = "cooling"
        }
    } else if (mode == "auto") {
        if (temperature < heatingSetpoint) {
            statusText = "Heating to ${heatingSetpoint}°${location.temperatureScale}"
            operatingState = "heating"
        } else if (temperature > coolingSetpoint) {
            statusText = "Cooling to ${coolingSetpoint}°${location.temperatureScale}"
            operatingState = "cooling"
        }
    } else if (mode == "off") {
        statusText = "Right Now: Off"
    } else {
        statusText = "?"
    }

    sendEvent("name": "thermostat", "value": statusText, "description": statusText, displayed: true)
    sendEvent("name": "thermostatOperatingState", "value": operatingState, "description": operatingState, displayed: false)
}

def generateActivityFeedsEvent(notificationMessage) {
    sendEvent(name: "notificationMessage", value: "$device.displayName $notificationMessage", descriptionText: "$device.displayName $notificationMessage", displayed: true)
}

// Get stored temperature from currentState in current local scale
def getTempInLocalScale(state) {
    def temp = device.currentState(state)
    def scaledTemp = convertTemperatureIfNeeded(temp.value.toBigDecimal(), temp.unit).toDouble()
    return (getTemperatureScale() == "F" ? scaledTemp.round(0).toInteger() : roundC(scaledTemp))
}

// Get/Convert temperature to current local scale
def getTempInLocalScale(temp, scale) {
    def scaledTemp = convertTemperatureIfNeeded(temp.toBigDecimal(), scale).toDouble()
    return (getTemperatureScale() == "F" ? scaledTemp.round(0).toInteger() : roundC(scaledTemp))
}

// Get stored temperature from currentState in device scale
def getTempInDeviceScale(state) {
    def temp = device.currentState(state)
    if (temp && temp.value && temp.unit) {
        return getTempInDeviceScale(temp.value.toBigDecimal(), temp.unit)
    }
    return 0
}

def getTempInDeviceScale(temp, scale) {
    if (temp && scale) {
        //API return/expects temperature values in F
        return ("F" == scale) ? temp : celsiusToFahrenheit(temp).toDouble().round(0).toInteger()
    }
    return 0
}

def roundC(tempC) {
    return (Math.round(tempC.toDouble() * 2)) / 2
}