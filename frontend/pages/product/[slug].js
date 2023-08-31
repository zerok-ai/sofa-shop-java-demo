import { Fragment, useEffect, useState } from "react"
import Head from "next/head"
import Button from "../../components/Button"
import Image from "../../components/Image"
import QuantityPicker from "../../components/QuantityPicker"
import { fetchInventory } from "../../utils/inventoryProvider"
import { slugify } from "../../utils/helpers"
import CartLink from "../../components/CartLink"
import {
  SiteContext,
  ContextProviderComponent,
} from "../../context/mainContext"
import { getProducts } from "../../services/dataservice"
import PincodeCheck from "../../components/PincodeCheck"
import { useRouter } from "next/router"
import PageHeader from "../../components/PageHeader"

const ItemView = (props) => {
  const [numberOfitems, updateNumberOfItems] = useState(1)
  const [error, setError] = useState(false)
  const [product, setProduct] = useState(null)
  const router = useRouter()
  const {
    context: { addToCart },
  } = props

  useEffect(() => {
    if (router.isReady) {
      const slug = router.query.slug.replace(/-/g, " ")
      const fetchProduct = async () => {
        try {
          const inventory = await fetchInventory()
          const prod = inventory.data.find(
            (item) => slugify(item.name) === slugify(slug)
          )
          setProduct(prod)
        } catch (err) {
          console.log({ err })
          setProduct(null)
        }
      }
      fetchProduct()
    }
  }, [router])

  function addItemToCart(product) {
    product["quantity"] = numberOfitems
    addToCart(product)
  }

  function increment() {
    updateNumberOfItems(numberOfitems + 1)
  }

  function decrement() {
    if (numberOfitems === 1) return
    updateNumberOfItems(numberOfitems - 1)
  }

  if (!product) {
    return (
      <Fragment>
        <PageHeader />
        <h6 className="mb-3" style={{ height: "60vh" }}>
          {error && "Could not fetch product."}
        </h6>
      </Fragment>
    )
  }
  const { price, image, name, description } = product

  return (
    <>
      <CartLink />
      <Head>
        <title>Jamstack ECommerce - {name}</title>
        <meta name="description" content={description} />
        <meta
          property="og:title"
          content={`Jamstack ECommerce - ${name}`}
          key="title"
        />
      </Head>
      <div
        className="
        sm:py-12
        md:flex-row
        py-4 w-full flex flex-1 flex-col my-0 mx-auto
      "
      >
        <div className="w-full md:w-1/2 h-120 flex flex-1 bg-light hover:bg-light-200">
          <div className="py-16 p10 flex flex-1 justify-center items-center">
            <Image src={image} alt="Inventory item" className="max-h-full" />
          </div>
        </div>
        <div className="pt-2 px-0 md:px-10 pb-8 w-full md:w-1/2">
          <h1
            className="
           sm:mt-0 mt-2 text-5xl font-light leading-large
          "
          >
            {name}
          </h1>
          <h2 className="text-2xl tracking-wide sm:py-8 py-6">${price}</h2>
          <p className="text-gray-600 leading-7">{description}</p>
          <div className="my-6 flex justify-between content-end">
            <QuantityPicker
              increment={increment}
              decrement={decrement}
              numberOfitems={numberOfitems}
            />
            <PincodeCheck name={name} />
          </div>
          <Button
            full
            title="Add to Cart"
            onClick={() => addItemToCart(product)}
          />
        </div>
      </div>
    </>
  )
}

function ItemViewWithContext(props) {
  return (
    <ContextProviderComponent>
      <SiteContext.Consumer>
        {(context) => <ItemView {...props} context={context} />}
      </SiteContext.Consumer>
    </ContextProviderComponent>
  )
}

export default ItemViewWithContext
