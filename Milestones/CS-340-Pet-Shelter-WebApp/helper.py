import base64
import os

class Helper(object):
    def __init__(self):
        pass

    """
    Get Application Name
    """
    def getAppName(self):
        return "Grazioso Salvare Dashboard"
    
    """
    Get Url
    """
    def getUrl(self):
        return "https://www.snhu.edu/"

    """
    Get Encoded Logo Image
    """
    def getImage(self):
        image_filename = 'Grazioso_Salvare_Logo.png'
        encoded_image  = ""
        if not os.path.exists(image_filename):
            print(f"WARNING: Logo file '{image_filename}' not found.")
            encoded_image = b""
        else:
            encoded_image = base64.b64encode(open(image_filename, 'rb').read())
        return encoded_image
    
    #############################################
    # Check string value if it's NOT valid string
    # return message Otherwise return string
    #############################################
    def check_and_replace(self, string, message):
        if string is None or len(string) == 0:
            return message
        return string
    