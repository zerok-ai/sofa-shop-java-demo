import Head from 'next/head'
import { titleIfy , slugify } from '../utils/helpers'
import { DisplayMedium } from '../components'
import CartLink from '../components/CartLink'
import { fetchInventory } from '../utils/inventoryProvider'
import { getProducts } from '../services/dataservice'
import { STATIC_INVENTORY } from "../utils/staticInventory"
import { Fragment, useEffect, useState } from "react"
import PageHeader from "../components/PageHeader"

function Categories() {
  const [categories, setCategories] = useState([])
  const [error, setError] = useState(false)
  useEffect(() => {
    const fetchData = async () => {
      setError(false)
      try {
        const rdata = await fetchInventory()
        const inventory = rdata.data
        const inventoryCategorized = inventory.reduce((acc, next) => {
          const categories = next.categories
          categories.forEach((c) => {
            const index = acc.findIndex((item) => item.name === c)
            if (index !== -1) {
              const item = acc[index]
              item.itemCount = item.itemCount + 1
              acc[index] = item
            } else {
              const item = {
                name: c,
                image: next.image,
                itemCount: 1,
              }
              acc.push(item)
            }
          })
          return acc
        }, [])
        setCategories(inventoryCategorized)
      } catch (err) {
        console.log({ err })
        setError(true)
      }
    }
    fetchData()
  }, [])
  if (!categories.length || error) {
    return (
      <Fragment>
        <PageHeader />
        <h6 className="mb-3" style={{ height: "80vh" }}>
          {error && "Could not fetch categories."}
        </h6>
      </Fragment>
    )
  }
  return (
    <>
      <div className="w-full">
        <CartLink />
        <Head>
          <title>Jamstack ECommerce - All Categories</title>
          <meta
            name="description"
            content={`Jamstack ECommerce - All categories`}
          />
          <meta
            property="og:title"
            content="Jamstack ECommerce - All Categories"
            key="title"
          />
        </Head>
        <div
          className="
          pt-4 sm:pt-10 pb-8
        "
        >
          <h1 className="text-5xl font-light">All categories</h1>
        </div>
        <div className="flex flex-col items-center">
          {/* <div className="my-4 lg:my-8 flex flex-col lg:flex-row justify-between"> */}
          <div
            className="grid gap-4
          lg:grid-cols-3 md:grid-cols-2 grid-cols-1"
          >
            {categories.map((category, index) => (
              <DisplayMedium
                key={index}
                imageSrc={category.image}
                subtitle={`${category.itemCount} items`}
                title={titleIfy(category.name)}
                link={`/category/${slugify(category.name)}`}
              />
            ))}
          </div>
        </div>
      </div>
    </>
  )
}


export default Categories