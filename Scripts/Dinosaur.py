# This module can be accessed by a python controller with
# its execution method set to 'Module'
# * Set the module string to "gamelogic_module.main" (without quotes)
# * When renaming the script it MUST have a .py extension
# * External text modules are supported as long as they are at
#   the same location as the blendfile or one of its libraries.

import bge
import math
import udpControllerV10

import BlenderData_pb2

# variables defined here will only be set once when the
# module is first imported. Set object specific vars
# inside the function if you intend to use the module
# with multiple objects.


def steering():
  
    cont = bge.logic.getCurrentController()
    own = cont.owner

    scene = bge.logic.getCurrentScene()
    objects = scene.objects

    targetProperty=own['target']
    actu = cont.actuators['Steering']
    
    if targetProperty != "none":
        own['movingTo'] = True
        own['reachedTarget'] = False
        
       
        #if target is water/vegetation
        if targetProperty in objects:
            #print(targetProperty)
            actu.target = targetProperty;
            cont.activate(actu)
            #print("Activated Cont")
            #own['target'] = "none"
        #else find dinosaur
        else:
            #print("Searching Dinos")
            for keys,values in bge.logic.objects.items():
                if keys == targetProperty:
                    targetProperty = values
                    #print(targetProperty)
                    actu.target = targetProperty;
                    cont.activate(actu)
                    #own['target'] = "none"
        

def reachedTarget():

    cont = bge.logic.getCurrentController()
    own = cont.owner
    sens = cont.sensors['Actuator']

    if sens.positive:

        if own['movingTo'] is True:

            if own['reachedTarget'] is False:

                own['movingTo'] = False
                own['reachedTarget'] = True

                if "Water" or "Cube" not in own['target']:
                    for keys,values in bge.logic.objects.items():
                        if keys == own['target']:
                            values['alive'] = False


                own['target'] = "none"
                
                blenderData = BlenderData_pb2.BlenderData()
                message = blenderData.command.add()
                message.commandType = "REACHEDTARGET"
                message.uniqueObjectName = own['uniqueName']
                message.posX = round(own.worldPosition.x,2)
                message.posY = round(own.worldPosition.y,2)

                udpControllerV10.sendReachedTarget(blenderData)

                #print("Sent Reached Target")



def collisionVegetation():

    cont = bge.logic.getCurrentController()
    own = cont.owner
    sens = cont.sensors['Collision1']
    actu = cont.actuators['Steering']
    
    if sens.positive:

        cont.deactivate(actu)

def collisionWater():

    cont = bge.logic.getCurrentController()
    own = cont.owner
    sens = cont.sensors['Collision']
    actu = cont.actuators['Steering']
    
    if sens.positive:

        cont.deactivate(actu)

def kill():

    cont = bge.logic.getCurrentController()
    own = cont.owner
    actu = cont.actuators['Steering']
    cont.deactivate(actu)
    own['target'] = "none"
    own['movingTo'] = False
    own['reachedTarget'] = True

    rotVec = [0,90,0]
    own.applyRotation(rotVec, 0)


    print(own['uniqueName'])
    print("Died")

    message = "< BLENDER > < KILL "
    message += "("
    message += own['uniqueName']
    message += ") "
    message += "("
    message += str(round(own.worldPosition.x,2))
    message += ","
    message += str(round(own.worldPosition.y,2))
    message += ") >"
    #udpControllerV9.sendReachedTarget(message)


def checkIsAlive():

    cont = bge.logic.getCurrentController()
    own = cont.owner

    if (own['alive'] == False) and (own['deathCounter'] > 0):

        newDeathCounter = own['deathCounter']
        own['deathCounter'] = newDeathCounter - 1


    if(own['deathCounter'] == 0):

        respawn()



def respawn():

    cont = bge.logic.getCurrentController()
    own = cont.owner

    #rotation = math.radians(0)
    rotation = 0
    orientationMatrix = own.worldOrientation.to_euler()
    orientationMatrix.y = rotation
    own.worldOrientation = orientationMatrix
    #values.applyRotation(rotation, False)

    own.worldPosition.x = 150
    own.worldPosition.y = 150

    own['alive'] = True
    own['consuming'] = False
    own['deathCounter'] = 30

    # tell java of respawn
    blenderData = BlenderData_pb2.BlenderData()
    message = blenderData.command.add()
    message.commandType = "RESPAWN"
    message.uniqueObjectName = own['uniqueName']
    message.posX = round(own.worldPosition.x,2)
    message.posY = round(own.worldPosition.y,2)

    udpControllerV10.sendReachedTarget(blenderData)






            
    