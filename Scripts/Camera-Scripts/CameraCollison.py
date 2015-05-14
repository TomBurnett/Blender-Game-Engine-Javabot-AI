from bge import logic

def collision(cont):
    
    own = cont.owner
    scene = logic.getCurrentScene()
    target = scene.objects['Cam_LookAt']
    ray = cont.sensors['CamCollision']
    
    if ray.positive:
        own.worldPosition = ray.hitPosition
    else:
            own.worldPosition = target.worldPosition
            own.localPosition.y -= ray.range