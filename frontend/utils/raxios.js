import axios from "axios"

const raxios = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
})

export default raxios
