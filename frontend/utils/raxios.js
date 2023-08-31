import axios from "axios"

const endpoint = process.env.NEXT_PUBLIC_API_URL ?? "/"

const raxios = axios.create({
  baseURL: endpoint,
})

export default raxios
