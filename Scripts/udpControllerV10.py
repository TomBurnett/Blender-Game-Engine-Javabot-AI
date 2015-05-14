#! /usr/bin/python

import socket
import sys
#sys.path.append("google/")

#protobuf_path='/Library/Python/2.7/site-packages/'

#sys.path.append(protobuf_path)

#import pkg_resources

import bge.logic as G
import pickle
import math
import struct

import google.protobuf.internal.decoder as decoder

import BlenderData_pb2
import Environment_pb2

#import re

# Bind the socket to the port
server_address = ('localhost', 10000)
listen_address = ('localhost', 10001)

#server_address = ('137.195.24.216', 10000)
#listen_address = ('137.195.120.86', 10001)

# Create a dictionary to hold created object references
G.objects = {}

dinosaurs = {}

javaReadyForData = False
frameRatePrintCount = 0


def init():

    # Create a UDP socket
    G.listener = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    G.sender = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

    G.listener.bind(listen_address)

    print ('starting up on %s port %s' % listen_address)

    try:

        packetData = G.listener.recv(10001)
        
        # Data Exists
        if packetData:

            blenderData = BlenderData_pb2.BlenderData()
            # Read length
            (size, position) = decoder._DecodeVarint(packetData, 0)
            
            # Read the message
            blenderData.ParseFromString(packetData[position:position+size])

            for command in blenderData.command:
                
                if(command.commandType == "JAVA-INITIALISE"):
                    
                    getEnvironmentObjects()
                else:
                    print("Error - initialisation packet not received")
                    G.endGame()
        else:
            G.endGame()
            #exit

    except:
        #No message to receive
        pass
 
    G.listener.setblocking(0)
    G.sender.setblocking(0)

   
def getEnvironmentObjects():

    scene = G.getCurrentScene()
    objects = scene.objects

    environment_objects = Environment_pb2.EnvironmentObjects()

    print("Getting Env Variables")

    counter = 0

    for object in objects:

        if "Water" in object.name:

            newWater = environment_objects.water.add()
            newWater.uniqueObjectName = object.name
            newWater.posX = object.worldPosition.x
            newWater.posY = object.worldPosition.y
            newWater.scaleX = object['scaleX']
            newWater.scaleY = object['scaleY']

        if "Cube" in object.name:

            newVeg = environment_objects.veg.add()
            newVeg.uniqueObjectName = object.name
            newVeg.posX = object.worldPosition.x
            newVeg.posY = object.worldPosition.y
            newVeg.scaleX = object['scaleX']
            newVeg.scaleY = object['scaleY']

        counter = counter + 1

        if(counter >= 10):

            G.sender.sendto(environment_objects.SerializeToString(), server_address)
            environment_objects = Environment_pb2.EnvironmentObjects()
            counter = 0

    G.sender.sendto(environment_objects.SerializeToString(), server_address)
    print("sent enviroment objects")


def listen():

    #Only used at initialisation
    replyWithDimensions = False
    counter = 0

    #Testing Only
    '''
    global frameRatePrintCount

    if(frameRatePrintCount < 1000):
        frameRate = G.getAverageFrameRate()
        if(frameRate < 100):
            frameRatePrintCount = frameRatePrintCount + 1
            print(G.getAverageFrameRate())
    '''

    try:
        while(counter < 5):
            counter = counter + 1

            packetData = G.listener.recv(10001)
            
            # Data Exists
            if packetData:

                dino_dimensions = BlenderData_pb2.BlenderData()

                blenderData = BlenderData_pb2.BlenderData()
                # Read length
                (size, position) = decoder._DecodeVarint(packetData, 0)
                
                # Read the message
                blenderData.ParseFromString(packetData[position:position+size])

                #print("Processing Command")
                for command in blenderData.command:
                    #print("loop")
                    if(command.commandType == "CREATE"):
                        #print("Create")
                        #new_dino_dimension = dino_dimensions.command.add()
                        dino_dimensions = createObject(command, dino_dimensions)

                        replyWithDimensions = True

                    elif(command.commandType == "NEWPOS"):
                        #print("NewPos")
                        updatePosition(command)
                    elif(command.commandType == "NEWTARGET"):
                        #print("new target recieved!")
                        newTarget(command)

                    elif(command.commandType == "DEAD"):
                        #print("Dinosaur Died")
                        killDino(command)

                    elif(command.commandType == "EATDRINK"):
                        consume(command)

                if blenderData.HasField('header'):
                    processHeader(blenderData.header)

                if replyWithDimensions is True:

                    sendProtobuf(dino_dimensions)
                    global javaReadyForData
                    javaReadyForData = True
                    #sendDinosaurDimensions(dino_dimensions)

    except:
        #No message to receive
        pass

def processHeader(header):

    ackPacket = BlenderData_pb2.BlenderData()
    ackPacket.header.sequenceNum = 1
    ackPacket.header.ack = header.sequenceNum
    ackPacket.header.bitField = 1

    sendProtobuf(ackPacket)


def processData(data):

    # Create command format:
    # < CREATE (objectName) (uniqueObjectName) (position) (velocity) (state)>

    if "CREATE" in data:

        createObject(data)

    elif "NEWPOSITION" in data:

        updatePosition(data)

    elif "NEWTARGET" in data:

        newTarget(data)

    else:
        print("Other command")

def createObject(command, dino_dimensions):

    cont = G.getCurrentController()
    obj = cont.owner
    scene = obj.scene # Get the scene the object is in

    objectName = command.object
    uniqueObjectName = command.uniqueObjectName

    posX = command.posX
    posY = command.posY

    newObject = scene.addObject(objectName, obj, 0)

    newObject.worldPosition.x = posX
    newObject.worldPosition.y = posY
    newObject.worldPosition.z = 0

    state = command.state

    newObject['name'] = uniqueObjectName
    newObject['uniqueName'] = uniqueObjectName
    G.objects[newObject['name']] = newObject
    dinosaurs[uniqueObjectName] = newObject


    if objectName == 't_rex':
        newObject.worldPosition.z = 2
        createChild(newObject, uniqueObjectName, 5, 6, state)

    if objectName == 'parasaur':

        createChild(newObject, uniqueObjectName, 4, 5, state)

    if objectName == 'diplodoc':
        
        createChild(newObject, uniqueObjectName, 10, 11, state)

    if objectName == 'gallimim':
        
        createChild(newObject, uniqueObjectName, 2, 3, state)

    new_dino_dimension = dino_dimensions.command.add()
    new_dino_dimension.commandType = "DIMENSIONS"
    new_dino_dimension.uniqueObjectName = uniqueObjectName
    new_dino_dimension.scaleX = newObject['scaleX']
    new_dino_dimension.scaleY = newObject['scaleY']
    

    return dino_dimensions


def createChild(parent, uniqueObjectName, zIcon, zText, state):
    
    cont = G.getCurrentController()
    obj = cont.owner
    scene = obj.scene # Get the scene the object is in

    #Set orb colour depending on state
    if state == 1:
        newIcon = scene.addObject("Icon_Red", obj, 0)
    elif state == 2:
        newIcon = scene.addObject("Icon_White", obj, 0)
    elif state == 3:
        newIcon = scene.addObject("Icon_Blue", obj, 0)
    elif state == 4:
        newIcon = scene.addObject("Icon_Green", obj, 0)
    elif state == 5:
        newIcon = scene.addObject("Icon_Black", obj, 0)
    else: 
        print("Dinosaur is in an undefined state")
        print(uniqueObjectName)

    newIcon.setParent(parent)
    newIcon.worldPosition.x = parent.worldPosition.x
    newIcon.worldPosition.y = parent.worldPosition.y
    newIcon.worldPosition.z = parent.scaling.z * zIcon

    iconText = scene.addObject("Text", obj, 0)
    iconText.setParent(parent)
    iconText.worldPosition.x = parent.worldPosition.x
    iconText.worldPosition.y = parent.worldPosition.y
    iconText.worldPosition.z = parent.scaling.z * zText

    iconText.text = uniqueObjectName

def sendDinosaurDimensions():

    return_dimensions = BlenderData_pb2.BlenderData()

    for keys, values in dinosaurs.items():
 
        newDimensionData = return_dimensions.command.add()
        newDimensionData.commandType = "DIMENSIONS"
        newDimensionData.uniqueObjectName = values['uniqueName']
        newDimensionData.scaleX = values['scaleX']
        newDimensionData.scaleY = values['scaleY']


    sent = G.sender.sendto(return_dimensions.SerializeToString(), server_address)

    global javaReadyForData
    javaReadyForData = True

    

def updatePosition(command):

    cont = G.getCurrentController()
    obj = cont.owner
    scene = obj.scene # Get the scene the object is in
    #scene = G.getCurrentScene()

    uniqueObjectName = command.uniqueObjectName

    posX = command.posX
    posY = command.posY

    velX = command.velX
    velY = command.velY

    state = command.state

    # set amount to move
    #movement = [ velX, velY, 0.0]

    # use world axis
    #local = False


    for keys, values in dinosaurs.items():
        if keys == uniqueObjectName:

            # move game object
            #values.applyMovement( movement, local)
            
            values.worldPosition.x = posX
            values.worldPosition.y = posY

            rotation = rotateObject(values, velX, velY)

            checkState(state, values)

            values['consuming'] = False
            #print("Position Updated")

def rotateObject(object, velocityX, velocityY):
    
    rotation = (-1) * (math.atan2(velocityY, velocityX))
    
    rotation = math.degrees(rotation)

    #orientate rotation based on pos/neg of angle
    if(velocityY < 0):
        rotation = (360 - rotation) + 90
    else:
        rotation = (rotation - 90) * (-1)

    rotation = math.radians(rotation)

    orientationMatrix = object.worldOrientation.to_euler()
    orientationMatrix.z = rotation
    object.worldOrientation = orientationMatrix

    return rotation


def checkState(state, parent):

    cont = G.getCurrentController()
    obj = cont.owner
    scene = obj.scene # Get the scene the object is in

    childList = parent.children

    #if state == 1 and "Icon_Red" in childList[]
    if state == 1:
        
        if "Icon_Red" not in childList:

            removeIcon(childList)

            newIcon = scene.addObject("Icon_Red", obj, 0)
            newIcon.setParent(parent)
            newIcon.worldPosition.x = parent.worldPosition.x
            newIcon.worldPosition.y = parent.worldPosition.y
            newIcon.worldPosition.z = parent.scaling.z * 6

    elif state == 2:
        
        if "Icon_White" not in childList:

            removeIcon(childList)

            newIcon = scene.addObject("Icon_White", obj, 0)
            newIcon.setParent(parent)
            newIcon.worldPosition.x = parent.worldPosition.x
            newIcon.worldPosition.y = parent.worldPosition.y
            newIcon.worldPosition.z = parent.scaling.z * 6
        
    elif state == 3:

        if "Icon_Blue" not in childList:

            removeIcon(childList)

            newIcon = scene.addObject("Icon_Blue", obj, 0)
            newIcon.setParent(parent)
            newIcon.worldPosition.x = parent.worldPosition.x
            newIcon.worldPosition.y = parent.worldPosition.y
            newIcon.worldPosition.z = parent.scaling.z * 6

    elif state == 4:

        if "Icon_Green" not in childList:

            removeIcon(childList)

            newIcon = scene.addObject("Icon_Green", obj, 0)
            newIcon.setParent(parent)
            newIcon.worldPosition.x = parent.worldPosition.x
            newIcon.worldPosition.y = parent.worldPosition.y
            newIcon.worldPosition.z = parent.scaling.z * 6

    elif state == 5:

        if "Icon_Black" not in childList:

            removeIcon(childList)

            newIcon = scene.addObject("Icon_Black", obj, 0)
            newIcon.setParent(parent)
            newIcon.worldPosition.x = parent.worldPosition.x
            newIcon.worldPosition.y = parent.worldPosition.y
            newIcon.worldPosition.z = parent.scaling.z * 6

    else: 
        print("Default State")



def removeIcon(childList):

    if "Icon_Blue" in childList:
        child = childList["Icon_Blue"]
        child.endObject()

    elif "Icon_Red" in childList:
        child = childList["Icon_Red"]
        child.endObject()

    elif "Icon_Green" in childList:
        child = childList["Icon_Green"]
        child.endObject()

    elif "Icon_White" in childList:
        child = childList["Icon_White"]
        child.endObject()

    elif "Icon_Black" in childList:
        child = childList["Icon_Black"]
        child.endObject()

def updateText(parent, rotation, velocityX, velocityY):
    
    #Update Text

    childList = parent.children
    child = childList["Text"]
    newText = 'R:' + str(round(rotation,2)) + 'X:' + str(round(velocityX,2)) + 'Y:' + str(round(velocityY,2))
    child.text = newText


def newTarget(command):

    cont = G.getCurrentController()
    obj = cont.owner
    scene = obj.scene # Get the scene the object is in

    uniqueObjectName = command.uniqueObjectName
    state = command.state
    target = command.target

    #find Dinosaur
    for keys, values in dinosaurs.items():
        if keys == uniqueObjectName:

            checkState(state, values)

            #Change target of dinosaur
            values['target'] = target

def killDino(command):

    uniqueObjectName = command.uniqueObjectName
    rotation = 90

    #find Dinosaur
    for keys, values in dinosaurs.items():
        if keys == uniqueObjectName:

            '''
            rotation = math.radians(rotation)
            orientationMatrix = values.worldOrientation.to_euler()
            orientationMatrix.y = rotation
            values.worldOrientation = orientationMatrix
            #values.applyRotation(rotation, False)
            '''

            values['alive'] = False


def consume(command):

    uniqueObjectName = command.uniqueObjectName
    state = command.state
    targetX = command.targetX
    targetY = command.targetY

    #find Dinosaur
    for keys, values in dinosaurs.items():
        if keys == uniqueObjectName:

            values['consuming'] = True
            #rotateObject(values, targetX, targetY)
            #values.applyRotation(rotation, False)
            #values['alive'] = False


def sendProtobuf(blenderData):

    try:
        sent = G.sender.sendto(blenderData.SerializeToString(), server_address)

        #print ( "sent sequence number: %s to %s" % (sequenceNum, server_address) )

    except:
        #No message to receive, do nothing
        pass
        #print ("Receive failed")

def sendPlayerPosition(protobuf):
    
    if javaReadyForData is True:
        
        try:
            sent = G.sender.sendto(protobuf.SerializeToString(), server_address)

            #print ( "sent sequence number: %s to %s" % (sequenceNum, server_address) )

        except:
            #No message to receive, do nothing
            pass
            #print ("Receive failed")

def sendReachedTarget(blenderData):

    try:
        #print("Sent Target Reached")
        sent = G.sender.sendto(blenderData.SerializeToString(), server_address)

        #print ( "sent sequence number: %s to %s" % (sequenceNum, server_address) )

    except:
        #No message to receive, do nothing
        pass
        #print ("Receive failed")


