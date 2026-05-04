import JSEncrypt from 'jsencrypt'

export function encrypt(txt: string, publicKey: string): string | false {
  const encryptor = new JSEncrypt()
  encryptor.setPublicKey(publicKey)
  return encryptor.encrypt(txt)
}

export function decrypt(txt: string, privateKey: string): string | false {
  const decryptor = new JSEncrypt()
  decryptor.setPrivateKey(privateKey)
  return decryptor.decrypt(txt)
}
