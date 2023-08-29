import Head from 'next/head'
import ListItem from '../../components/ListItem'
import { titleIfy, slugify } from '../../utils/helpers'
import fetchCategories from '../../utils/categoryProvider'
import inventoryForCategory from '../../utils/inventoryForCategory'
import CartLink from '../../components/CartLink'
import { Fragment, useEffect, useState } from "react"
import PageHeader from "../../components/PageHeader"
import { useRouter } from "next/router"

const Category = (props) => {
  const router = useRouter()

  const [inventory, setInventory] = useState([])
  const [error, setError] = useState(false)
  const { name } = router.query
  const title = name ? name.replace(/-/g, " ") : ""
  useEffect(() => {
    if (router.isReady && title) {
      setInventory([])
      try {
        setError(false)
        const fetchData = async () => {
          setError(false)
          const inventory = await inventoryForCategory(title)
          setInventory(inventory)
        }
        fetchData()
      } catch (err) {
        console.log({ err })
        setError(true)
      }
    }
  }, [router, title])
  if (!inventory.length || error) {
    return (
      <Fragment>
        <PageHeader />
        <div className="pt-4 sm:pt-10 pb-8">
          <h1 className="text-5xl font-light">{titleIfy(title)}</h1>
        </div>
        <h6 className="mb-3" style={{ height: "60vh" }}>
          {error && "Could not fetch category."}
        </h6>
      </Fragment>
    )
  }

  return (
    <>
      <CartLink />
      <Head>
        <title>Jamstack ECommerce - {title}</title>
        <meta name="description" content={`Jamstack ECommerce - ${title}`} />
        <meta
          property="og:title"
          content={`Jamstack ECommerce - ${title}`}
          key="title"
        />
      </Head>
      <div className="flex flex-col items-center">
        <div className="max-w-fw flex flex-col w-full">
          <div className="pt-4 sm:pt-10 pb-8">
            <h1 className="text-5xl font-light">{titleIfy(title)}</h1>
          </div>

          <div>
            <div className="flex flex-1 flex-wrap flex-row">
              {inventory.map((item, index) => {
                return (
                  <ListItem
                    key={index}
                    link={`/product/${slugify(item.name)}`}
                    title={item.name}
                    price={item.price}
                    imageSrc={item.image}
                  />
                )
              })}
            </div>
          </div>
        </div>
      </div>
    </>
  )
}


export default Category