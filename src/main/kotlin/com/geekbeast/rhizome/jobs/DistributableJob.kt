package com.geekbeast.rhizome.jobs

import java.util.concurrent.Callable

/**
 *
 * @author Matthew Tamayo-Rios &lt;matthew@openlattice.com&gt;
 */
interface DistributableJob<R> : Callable<R?> {
    val resumable: Boolean
}