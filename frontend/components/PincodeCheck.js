import React from "react"
import styles from "./styles.module.css"
import raxios from "../utils/raxios"
import { PRODUCT_AVAILABILITY_ENDPOINT } from "../utils/endpoints"
import { useRouter } from "next/router"
import { toast } from "react-toastify"
import { getSKU } from "../services/dataservice"
const PincodeCheck = ({ name }) => {
  const [pincode, setPincode] = React.useState("")
  const [status, setStatus] = React.useState("")
  const router = useRouter()
  const handleSubmit = async (e) => {
    e.preventDefault()
    if (pincode.length !== 6) {
      setStatus("invalid")
      return
    }
    try {
      setStatus("loading")
      await raxios.get(
        PRODUCT_AVAILABILITY_ENDPOINT.replace("{pincode}", pincode).replace(
          "{sku}",
          getSKU(name)
        )
      )
      setStatus("success")
    } catch (err) {
      toast.error("Something went wrong while checking availability.")
      setStatus("exception")
    }
  }

  const getStatus = () => {
    if (!status) return null;
    if (status === "error") {
      return (
        <p className={styles.error}>
          Sorry, product does not ship to your location.
        </p>
      )
    }
    if (status === "success") {
      return <p className={styles.success}>Product ships to your location!</p>
    }
    if (status === "invalid") {
      return <p className={styles.error}>Please enter a valid pincode.</p>
    }
    if (status === "exception") {
      return (
        <p className={styles.error}>
          Something went wrong while checking availability.
        </p>
      )
    }
  }

  return (
    <div className="flex">
      <form onSubmit={handleSubmit}>
        <div className="flex flex-col">
          <p className="text-sm my-1">Check Availability</p>
          <input
            type="text"
            className={styles.input}
            value={pincode}
            disabled={status === "loading"}
            onChange={(e) => setPincode(e.target.value)}
          />
          {getStatus()}
        </div>
      </form>
    </div>
  )
}

export default PincodeCheck
