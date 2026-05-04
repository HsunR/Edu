import JSEncrypt from 'jsencrypt'

export function encrypt(txt: string): string | null {
  const publicKey = import.meta.env.VITE_APP_RSA_PUBLIC_KEY
  if (!publicKey) return null
  const encryptor = new JSEncrypt()
  encryptor.setPublicKey(publicKey)
  return encryptor.encrypt(txt) || null
}

export function decrypt(txt: string): string | null {
  const privateKey = import.meta.env.VITE_APP_RSA_PRIVATE_KEY
  if (!privateKey) return null
  const encryptor = new JSEncrypt()
  encryptor.setPrivateKey(privateKey)
  return encryptor.decrypt(txt) || null
}
