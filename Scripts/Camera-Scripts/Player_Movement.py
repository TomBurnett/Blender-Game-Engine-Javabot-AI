import bge


def main():

    cont = bge.logic.getCurrentController()
    Player = cont.owner
    own = cont.owner
    keyboard = bge.logic.keyboard
    wKey = bge.logic.KX_INPUT_ACTIVE == keyboard.events[bge.events.WKEY]
    sKey = bge.logic.KX_INPUT_ACTIVE == keyboard.events[bge.events.SKEY]
    aKey = bge.logic.KX_INPUT_ACTIVE == keyboard.events[bge.events.AKEY]
    dKey = bge.logic.KX_INPUT_ACTIVE == keyboard.events[bge.events.DKEY]
    shift = bge.logic.KX_INPUT_ACTIVE == keyboard.events[bge.events.LEFTSHIFTKEY]
    space = bge.logic.KX_INPUT_ACTIVE == keyboard.events[bge.events.SPACEKEY]
    
    """
    if wKey:
        Player.applyMovement((0, 0.1, 0), True)
            
    if sKey:
        Player.applyMovement((0, -0.1, 0), True)
    if aKey:
        Player.applyMovement((-0.1, 0, 0), True)
    if dKey:
        Player.applyMovement((0.1, 0, 0), True) 
        
    if wKey and shift: 
        Player.applyMovement((0, 0.2, 0), True)

    """

    if own['active'] is True:

        if wKey:
            Player.applyMovement((-0.4, 0, 0), True)
                
        if sKey:
            Player.applyMovement((0.4, 0, 0), True)
        if aKey:
            Player.applyMovement((0, 0, 0.4), True)
        if dKey:
            Player.applyMovement((0, 0, -0.4), True) 
            
        if space: 
            Player.applyMovement((0, 0.2, 0), True)

main()