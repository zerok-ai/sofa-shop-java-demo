import React from 'react'
import AddInventory from '../components/formComponents/AddInventory'
import ViewInventory from '../components/ViewInventory'
import raxios from "../utils/raxios"
import { toast } from "react-toastify"
import { LIST_INVENTORY_ENDPOINT } from "../utils/endpoints"
import axios from "axios"

class Inventory extends React.Component {
  state = {
    viewState: "view",
  }
  toggleViewState(viewState) {
    this.setState(() => ({ viewState }))
  }
  bulkUpload() {
    axios
      .get("/bulk.json")
      .then((res) => {
        const data = res.data
        raxios
          .post("/api/product/all", data)
          .then((res) => {
            toast.success("Inventory uploaded")
          })
          .catch((err) => {
            toast.error("Could not upload inventory")
          })
      })
      .catch((err) => {
        toast.error("Could not upload inventory")
      })
  }
  render() {
    return (
      <div>
        <div className="flex my-12">
          <p
            role="button"
            className="mr-4 cursor-pointer hover:text-primary"
            onClick={() => this.toggleViewState("view")}
          >
            View Inventory
          </p>
          <p
            role="button"
            className="cursor-pointer hover:text-primary mr-4 cursor-pointer hover:text-primary"
            onClick={() => this.toggleViewState("add")}
          >
            Add Item
          </p>
          <p
            role="button"
            className="cursor-pointer hover:text-primary"
            onClick={() => this.bulkUpload()}
          >
            Bulk Upload
          </p>
        </div>
        {this.state.viewState === "view" ? <ViewInventory /> : <AddInventory />}
        <button
          onClick={this.props.signOut}
          className="mt-4 bg-primary hover:bg-black text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
          type="button"
        >
          Sign Out
        </button>
      </div>
    )
  }
}

export default Inventory